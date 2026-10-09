# TTS Demo API contract

Java 17+ backend, same-origin static HTML/CSS/JavaScript frontend. Local URL: http://localhost:8787. No frontend build step, CDN, or exposed API keys.

## GET /api/config

200 application/json:

```json
{
  "maxTextLength": 3000,
  "providers": [
    {
      "id": "gemini",
      "name": "Gemini 3.8 Flash TTS",
      "model": "gemini-3.8-flash-tts",
      "configured": true,
      "supportsStyle": true,
      "supportsRate": false,
      "defaultVoice": "Kore",
      "voices": [{"id": "Kore", "name": "Kore · Firm", "language": "multilingual"}]
    },
    {
      "id": "edge",
      "name": "Microsoft Edge 免费语音",
      "model": "edge-read-aloud",
      "configured": true,
      "supportsStyle": false,
      "supportsRate": true,
      "defaultVoice": "zh-CN-XiaoxiaoNeural",
      "voices": [{"id": "zh-CN-XiaoxiaoNeural", "name": "晓晓 · 中文女声", "language": "zh-CN"}]
    }
  ]
}
```

The actual voice arrays contain more voices. configured means a key is present (Gemini) or no key is required (Edge); it does NOT assert remote availability. UI must explain missing Gemini configuration and disable synthesis for that provider if configured=false. Use provider metadata rather than inventing model identifiers.

## POST /api/tts

Content-Type: application/json

```json
{
  "provider": "gemini",
  "text": "你好，这是语音合成测试。",
  "voice": "Kore",
  "style": "自然、温暖地朗读",
  "rate": 0
}
```

- provider: required, gemini or edge.
- text: required, trim then 1–3000 UTF-16 code units. Frontend textarea maxlength=3000 and character count.
- voice: optional; defaults to the selected provider's defaultVoice. Must belong to that provider's voices.
- style: optional, at most 500 characters, Gemini only. Hide or disable for Edge.
- rate: optional integer, -50 through 100 (percentage change), Edge only. Hide or disable for Gemini. Default 0.
- 200: **binary audio body**, not JSON. Gemini audio/wav, Edge audio/mpeg. Headers Content-Disposition: attachment; filename="tts-gemini.wav" or "tts-edge.mp3"; X-TTS-Provider; X-TTS-Model; Cache-Control: no-store.
- Browser fetch response.blob(), create object URL, display native audio controls, attempt playback but handle autoplay blocking by showing a play hint. Provide download link and use response content type to choose .wav/.mp3.
- Revoke old object URL when replaced. Disable submit while pending, show elapsed time/loading, AbortController timeout at 150 seconds, handle failures and restore controls.
- Failures 400 invalid request, 413 too large, 429 busy/upstream quota, 502 upstream failure, 503 missing Gemini key, 504 timeout. All failure bodies: {"error":{"code":"VALIDATION_ERROR","message":"用户可读的错误说明"}}. Read error.message and render as text, never HTML. Retain previous successful audio after a failed request but identify it as the previous result.

## GET /api/health

200 {"status":"ok"}. No secrets, no remote probe.

## Frontend deliverable

Only create/edit src/main/resources/static/index.html, style.css, app.js (or inline CSS/JS in index.html). Pure HTML/CSS/vanilla JS. Chinese UI, responsive desktop/mobile, clean polished voice studio, provider selection, voice selection, textarea, optional Gemini delivery style and Edge rate, generation button, clear pending/error states, playback and download. First usable provider defaults to Edge so the free path works immediately. Include short Chinese/Japanese/English sample text buttons if useful. Never read or display environment variables; never add a backend or subprocess. Parent agent owns Java, Maven, docs and scripts. No git commits.
