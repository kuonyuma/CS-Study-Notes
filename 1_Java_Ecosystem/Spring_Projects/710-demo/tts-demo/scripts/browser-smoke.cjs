// Headless application regression test; does not use the user's browser profile.
const { chromium } = require('../.tools/node_modules/playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');

(async () => {
  const browser = await chromium.launch({ channel: 'msedge', headless: true });
  const context = await browser.newContext({ viewport: { width: 1440, height: 1100 }, acceptDownloads: true });
  const page = await context.newPage();
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  try {
    await page.goto('http://localhost:8787');
    await page.locator('#voiceSelect').waitFor();
    await page.waitForFunction(() => document.querySelector('#voiceSelect').value === 'zh-CN-XiaoxiaoNeural');
    await page.locator('#textInput').fill('你好，这是浏览器播放测试。');

    async function generate(provider, extension) {
      await page.locator('[data-id="' + provider + '"]').click();
      const responseReady = page.waitForResponse(r => r.url().endsWith('/api/tts'), { timeout: 145000 });
      await page.locator('#generateBtn').click();
      assert.equal(await page.locator('#generateBtn').isDisabled(), true);
      const response = await responseReady;
      assert.equal(response.status(), 200, response.status() === 200 ? 'audio' : await response.text());
      await page.locator('#stateResult').waitFor({ state: 'visible' });
      await page.waitForFunction(() => {
        const player = document.querySelector('#audioPlayer');
        return player.readyState >= 1 && Number.isFinite(player.duration) && player.duration > 0;
      }, null, { timeout: 10000 });
      const audio = await page.locator('#audioPlayer').evaluate(e => ({ duration: e.duration, error: e.error?.code ?? null }));
      assert.equal(audio.error, null);
      assert.match(await page.locator('#downloadBtn').getAttribute('download'), new RegExp('\\.' + extension + '$'));
      const downloadReady = page.waitForEvent('download');
      await page.locator('#downloadBtn').click();
      const download = await downloadReady;
      assert.ok(fs.statSync(await download.path()).size > 100);
      await page.locator('#audioPlayer').evaluate(e => e.pause());
      console.log(JSON.stringify({ provider, status: 200, playableSeconds: audio.duration, format: extension }));
    }

    await generate('edge', 'mp3');
    await generate('gemini', 'wav');
    for (const link of await page.locator('#historyList a[download]').all()) {
      const valid = await link.evaluate(async e => { try { return (await fetch(e.href)).ok; } catch { return false; } });
      assert.equal(valid, true, 'Every visible history download must remain usable after another generation');
    }
    await page.screenshot({ path: '.local/desktop.png', fullPage: true });
    await page.setViewportSize({ width: 390, height: 844 });
    const widths = await page.evaluate(() => ({ viewport: innerWidth, document: document.documentElement.scrollWidth }));
    assert.ok(widths.document <= widths.viewport, JSON.stringify(widths));
    await page.screenshot({ path: '.local/mobile.png', fullPage: true });
    await page.setViewportSize({ width: 1440, height: 1100 });
    const savedAudio = await page.locator('#audioPlayer').getAttribute('src');
    await page.route('**/api/tts', route => route.fulfill({ status: 502, contentType: 'application/json', body: JSON.stringify({ error: { code: 'TEST_UPSTREAM_FAILURE', message: '<script>unsafe</script>测试错误' } }) }));
    await page.locator('#generateBtn').click();
    await page.locator('#errorCard').waitFor({ state: 'visible' });
    assert.equal(await page.locator('#errorMessage').textContent(), '<script>unsafe</script>测试错误');
    assert.equal(await page.locator('#errorMessage script').count(), 0);
    assert.equal(await page.locator('#audioPlayer').getAttribute('src'), savedAudio);
    assert.match(await page.locator('#resultStatusText').textContent(), /上一次/);
    assert.equal(await page.locator('#generateBtn').isEnabled(), true);
    await page.unroute('**/api/tts');
    await page.locator('#dismissErrorBtn').click();
    await page.route('**/api/config', async route => {
      const configResponse = await route.fetch();
      const config = await configResponse.json();
      config.providers.find(p => p.id === 'gemini').configured = false;
      await route.fulfill({ response: configResponse, json: config });
    });
    await page.reload();
    await page.locator('[data-id="gemini"]').click();
    await page.locator('#providerNotice').waitFor({ state: 'visible' });
    assert.equal(await page.locator('#generateBtn').isDisabled(), true);
    await page.locator('[data-id="edge"]').click();
    await page.locator('#textInput').fill('免费语音');
    assert.equal(await page.locator('#generateBtn').isEnabled(), true);
    assert.deepEqual(errors, []);
    console.log('PASS: Edge + Gemini audio decode, downloads, pending state, safe error rendering, previous result retention, desktop/mobile layout, no browser JS errors.');
  } finally { await browser.close(); }
})().catch(error => { console.error(error); process.exitCode = 1; });
