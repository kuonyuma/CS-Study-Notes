/**
 * TTS 语音合成工坊 (Voice Studio) 前端核心逻辑
 * 纯原生 JavaScript 实现，严格遵守 api-contract.md 协议规范
 */

'use strict';

(function () {
  // =========================================================================
  // 应用全局状态
  // =========================================================================
  const state = {
    config: null,
    selectedProviderId: 'edge', // 默认优先选用 Edge 免费引擎
    selectedVoiceId: '',
    maxTextLength: 3000,
    isGenerating: false,
    abortController: null,
    timerIntervalId: null,
    startTime: 0,
    currentAudioUrl: null,
    currentBlob: null,
    previousAudioMeta: null,
    hasCurrentResult: false,
    historyList: []
  };

  // 示例预置文本
  const SAMPLE_TEXTS = {
    zh: '欢迎体验智能语音合成工坊。无论是有声书朗读、自媒体视频配音，还是无障碍交互，都能为您呈现细腻自然、富有沉浸感的听觉体验。',
    en: 'Welcome to the Voice Synthesis Studio. Experience high-quality, expressive, and natural speech synthesis powered by cutting-edge AI models.',
    ja: '音声合成スタジオへようこそ。自然で表現力豊かなAI音声をお届けします。テキストを入力して、手軽に高品質な音声をお試しください。'
  };

  // =========================================================================
  // DOM 元素引用
  // =========================================================================
  const elements = {
    // 顶部状态
    serviceStatusPill: document.getElementById('serviceStatusPill'),
    serviceStatusText: document.getElementById('serviceStatusText'),

    // 服务商与音色
    providerCards: document.getElementById('providerCards'),
    providerNotice: document.getElementById('providerNotice'),
    providerNoticeTitle: document.getElementById('providerNoticeTitle'),
    providerNoticeDesc: document.getElementById('providerNoticeDesc'),
    voiceSelect: document.getElementById('voiceSelect'),
    voiceLanguageTag: document.getElementById('voiceLanguageTag'),
    voiceModelTag: document.getElementById('voiceModelTag'),

    // 参数控制
    edgeRateGroup: document.getElementById('edgeRateGroup'),
    rateSlider: document.getElementById('rateSlider'),
    rateValueDisplay: document.getElementById('rateValueDisplay'),
    geminiStyleGroup: document.getElementById('geminiStyleGroup'),
    styleInput: document.getElementById('styleInput'),
    styleCountDisplay: document.getElementById('styleCountDisplay'),

    // 文本输入
    textInput: document.getElementById('textInput'),
    charCount: document.getElementById('charCount'),
    maxCharCount: document.getElementById('maxCharCount'),
    sampleZhBtn: document.getElementById('sampleZhBtn'),
    sampleEnBtn: document.getElementById('sampleEnBtn'),
    sampleJaBtn: document.getElementById('sampleJaBtn'),
    clearTextBtn: document.getElementById('clearTextBtn'),

    // 操作按钮
    generateBtn: document.getElementById('generateBtn'),
    generateBtnText: document.getElementById('generateBtnText'),
    cancelBtn: document.getElementById('cancelBtn'),

    // 交付区域状态
    deliveryBadge: document.getElementById('deliveryBadge'),
    stateIdle: document.getElementById('stateIdle'),
    stateGenerating: document.getElementById('stateGenerating'),
    stateResult: document.getElementById('stateResult'),
    elapsedTime: document.getElementById('elapsedTime'),

    // 播放与结果
    resultBanner: document.getElementById('resultBanner'),
    resultStatusIcon: document.getElementById('resultStatusIcon'),
    resultStatusText: document.getElementById('resultStatusText'),
    resultTimestamp: document.getElementById('resultTimestamp'),
    metaProvider: document.getElementById('metaProvider'),
    metaFormat: document.getElementById('metaFormat'),
    metaSize: document.getElementById('metaSize'),
    metaDuration: document.getElementById('metaDuration'),
    autoplayNotice: document.getElementById('autoplayNotice'),
    audioPlayer: document.getElementById('audioPlayer'),
    downloadBtn: document.getElementById('downloadBtn'),
    downloadFormatLabel: document.getElementById('downloadFormatLabel'),

    // 错误展示
    errorCard: document.getElementById('errorCard'),
    errorCodeBadge: document.getElementById('errorCodeBadge'),
    errorMessage: document.getElementById('errorMessage'),
    errorAdvice: document.getElementById('errorAdvice'),
    dismissErrorBtn: document.getElementById('dismissErrorBtn'),

    // 历史列表
    historyList: document.getElementById('historyList'),
    clearHistoryBtn: document.getElementById('clearHistoryBtn')
  };

  // =========================================================================
  // 初始化函数
  // =========================================================================
  function init() {
    bindEvents();
    checkHealth();
    fetchConfig();
  }

  // =========================================================================
  // 事件绑定
  // =========================================================================
  function bindEvents() {
    // 文本域字符计数与快捷键
    elements.textInput.addEventListener('input', handleTextInput);
    elements.textInput.addEventListener('keydown', function (e) {
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        if (!state.isGenerating) {
          handleGenerate();
        }
      }
    });

    // 预置示例按钮
    elements.sampleZhBtn.addEventListener('click', function () {
      setText(SAMPLE_TEXTS.zh);
    });
    elements.sampleEnBtn.addEventListener('click', function () {
      setText(SAMPLE_TEXTS.en);
    });
    elements.sampleJaBtn.addEventListener('click', function () {
      setText(SAMPLE_TEXTS.ja);
    });
    elements.clearTextBtn.addEventListener('click', function () {
      setText('');
      elements.textInput.focus();
    });

    // 发音人选择变动
    elements.voiceSelect.addEventListener('change', function () {
      state.selectedVoiceId = this.value;
      updateVoiceTags();
    });

    // 语速滑块事件 (Edge)
    elements.rateSlider.addEventListener('input', function () {
      updateRateDisplay(parseInt(this.value, 10));
      clearActiveChips(elements.edgeRateGroup);
    });

    // 语速快捷预设芯片
    const rateChips = elements.edgeRateGroup.querySelectorAll('.chip-btn');
    rateChips.forEach(function (btn) {
      btn.addEventListener('click', function () {
        const rate = parseInt(this.getAttribute('data-rate'), 10);
        elements.rateSlider.value = rate;
        updateRateDisplay(rate);
        highlightChip(elements.edgeRateGroup, this);
      });
    });

    // Gemini 风格字数统计与快捷标签
    elements.styleInput.addEventListener('input', function () {
      elements.styleCountDisplay.textContent = this.value.length + ' / 500';
    });
    const styleChips = elements.geminiStyleGroup.querySelectorAll('.chip-btn');
    styleChips.forEach(function (btn) {
      btn.addEventListener('click', function () {
        const style = this.getAttribute('data-style') || '';
        elements.styleInput.value = style;
        elements.styleCountDisplay.textContent = style.length + ' / 500';
      });
    });

    // 生成与取消按钮
    elements.generateBtn.addEventListener('click', handleGenerate);
    elements.cancelBtn.addEventListener('click', handleCancel);

    // 错误关闭按钮
    elements.dismissErrorBtn.addEventListener('click', function () {
      elements.errorCard.style.display = 'none';
    });

    // 清空历史按钮
    elements.clearHistoryBtn.addEventListener('click', function () {
      state.historyList = [];
      renderHistoryList();
    });

    // 页面卸载前释放 Object URL 避免内存泄露
    window.addEventListener('beforeunload', function () {
      if (state.currentAudioUrl) {
        URL.revokeObjectURL(state.currentAudioUrl);
      }
    });
  }

  // =========================================================================
  // 健康检查 /api/health
  // =========================================================================
  async function checkHealth() {
    try {
      const res = await fetch('/api/health', { method: 'GET', cache: 'no-store' });
      if (res.ok) {
        setHealthStatus(true, '服务就绪');
      } else {
        setHealthStatus(false, '服务异常 (' + res.status + ')');
      }
    } catch (err) {
      setHealthStatus(false, '连接后端失败');
    }
  }

  function setHealthStatus(isOnline, text) {
    elements.serviceStatusPill.className = 'status-pill ' + (isOnline ? 'status-online' : 'status-offline');
    elements.serviceStatusText.textContent = text;
  }

  // =========================================================================
  // 获取配置 /api/config
  // =========================================================================
  async function fetchConfig() {
    try {
      const res = await fetch('/api/config', { method: 'GET', cache: 'no-store' });
      if (!res.ok) {
        throw new Error('获取配置失败 (HTTP ' + res.status + ')');
      }
      const data = await res.json();
      state.config = data;
      state.maxTextLength = data.maxTextLength || 3000;
      elements.textInput.maxLength = state.maxTextLength;
      elements.maxCharCount.textContent = String(state.maxTextLength);

      applyConfig(data);
    } catch (err) {
      showConfigError(err.message || '无法获取引擎配置，请确保后端服务正常运行。');
    }
  }

  function applyConfig(config) {
    if (!config.providers || config.providers.length === 0) {
      showConfigError('后端未返回可用语音合成提供商。');
      return;
    }

    // 规范要求：优先默认选中 Edge 免费提供商；若无 Edge 则选首个配置有效的提供商
    const hasEdge = config.providers.some(function (p) { return p.id === 'edge'; });
    if (hasEdge) {
      state.selectedProviderId = 'edge';
    } else {
      const firstConfigured = config.providers.find(function (p) { return p.configured; });
      state.selectedProviderId = firstConfigured ? firstConfigured.id : config.providers[0].id;
    }

    renderProviderCards(config.providers);
    syncProviderState();
  }

  function renderProviderCards(providers) {
    elements.providerCards.innerHTML = '';

    providers.forEach(function (p) {
      const card = document.createElement('div');
      card.className = 'provider-card' + (p.id === state.selectedProviderId ? ' active' : '');
      card.setAttribute('role', 'radio');
      card.setAttribute('aria-checked', String(p.id === state.selectedProviderId));
      card.setAttribute('data-id', p.id);
      card.setAttribute('tabindex', '0');

      if (!p.configured) {
        card.classList.add('unconfigured');
      }

      // 卡片头部
      const header = document.createElement('div');
      header.className = 'provider-card-header';

      const title = document.createElement('span');
      title.className = 'provider-title';
      title.textContent = p.name;
      header.appendChild(title);

      const badge = document.createElement('span');
      badge.className = 'provider-badge';
      if (!p.configured) {
        badge.classList.add('badge-unconfigured');
        badge.textContent = '未配置密钥';
      } else if (p.id === 'edge') {
        badge.classList.add('badge-free');
        badge.textContent = '免费内置';
      } else {
        badge.classList.add('badge-ai');
        badge.textContent = 'AI 引擎';
      }
      header.appendChild(badge);
      card.appendChild(header);

      // 模型标识
      const model = document.createElement('span');
      model.className = 'provider-model';
      model.textContent = p.model || p.id;
      card.appendChild(model);

      // 点击切换（生成中忽略）
      card.addEventListener('click', function () {
        if (!state.isGenerating) {
          selectProvider(p.id);
        }
      });

      // 键盘导航支持（Space / Enter 选择，生成中忽略）
      card.addEventListener('keydown', function (e) {
        if (e.key === ' ' || e.key === 'Enter') {
          e.preventDefault();
          if (!state.isGenerating) {
            selectProvider(p.id);
          }
        }
      });

      elements.providerCards.appendChild(card);
    });
  }

  function selectProvider(providerId) {
    if (state.isGenerating) return;
    state.selectedProviderId = providerId;

    // 更新卡片激活态
    const allCards = elements.providerCards.querySelectorAll('.provider-card');
    allCards.forEach(function (card) {
      const id = card.getAttribute('data-id');
      const isSelected = id === providerId;
      card.classList.toggle('active', isSelected);
      card.setAttribute('aria-checked', String(isSelected));
    });

    syncProviderState();
  }

  function syncProviderState() {
    const provider = getCurrentProvider();
    if (!provider) return;

    // 1. 密钥未配置警告提示
    if (!provider.configured) {
      elements.providerNotice.style.display = 'flex';
      elements.providerNoticeTitle.textContent = provider.name + ' 未就绪';
      elements.providerNoticeDesc.textContent =
        '后端未检测到该服务商所需的 API 密钥，请在后端配置 GEMINI_API_KEY 或切换至免费的 Edge 引擎。';
    } else {
      elements.providerNotice.style.display = 'none';
    }

    // 2. 动态调节参数显示隐藏
    // supportsStyle -> 显示 Gemini Style 输入
    if (provider.supportsStyle) {
      elements.geminiStyleGroup.style.display = 'flex';
    } else {
      elements.geminiStyleGroup.style.display = 'none';
    }

    // supportsRate -> 显示 Edge Rate 滑块
    if (provider.supportsRate) {
      elements.edgeRateGroup.style.display = 'flex';
    } else {
      elements.edgeRateGroup.style.display = 'none';
    }

    // 3. 动态更新声音选择列表
    renderVoices(provider);

    // 4. 更新提交按钮可用性
    updateGenerateButtonState();
  }

  function renderVoices(provider) {
    elements.voiceSelect.innerHTML = '';
    const voices = provider.voices || [];

    if (voices.length === 0) {
      const opt = document.createElement('option');
      opt.value = '';
      opt.textContent = '默认发音人';
      elements.voiceSelect.appendChild(opt);
      state.selectedVoiceId = '';
    } else {
      let targetVoiceId = provider.defaultVoice || voices[0].id;
      // 检查当前选中的 voice 是否属于该 provider
      const hasCurrent = voices.some(function (v) { return v.id === state.selectedVoiceId; });
      if (hasCurrent) {
        targetVoiceId = state.selectedVoiceId;
      }

      voices.forEach(function (v) {
        const opt = document.createElement('option');
        opt.value = v.id;
        opt.textContent = v.name + (v.language ? ' (' + v.language + ')' : '');
        if (v.id === targetVoiceId) {
          opt.selected = true;
        }
        elements.voiceSelect.appendChild(opt);
      });

      state.selectedVoiceId = targetVoiceId;
    }

    updateVoiceTags();
  }

  function updateVoiceTags() {
    const provider = getCurrentProvider();
    if (!provider) return;

    const voices = provider.voices || [];
    const currentVoice = voices.find(function (v) { return v.id === state.selectedVoiceId; });

    if (currentVoice && currentVoice.language) {
      elements.voiceLanguageTag.textContent = currentVoice.language;
    } else {
      elements.voiceLanguageTag.textContent = '多语言/默认';
    }

    elements.voiceModelTag.textContent = provider.model || provider.id;
  }

  function getCurrentProvider() {
    if (!state.config || !state.config.providers) return null;
    return state.config.providers.find(function (p) { return p.id === state.selectedProviderId; }) || null;
  }

  // =========================================================================
  // 界面状态与控件操作
  // =========================================================================
  function handleTextInput() {
    const len = elements.textInput.value.length;
    elements.charCount.textContent = String(len);

    if (len > state.maxTextLength) {
      elements.charCount.parentElement.classList.add('warning');
    } else {
      elements.charCount.parentElement.classList.remove('warning');
    }

    updateGenerateButtonState();
  }

  function setText(str) {
    elements.textInput.value = str;
    handleTextInput();
  }

  function updateRateDisplay(rate) {
    let desc = '标准语速';
    if (rate > 0) desc = '+' + rate + '% 较快';
    else if (rate < 0) desc = rate + '% 较慢';
    else desc = '0% (标准语速)';

    elements.rateValueDisplay.textContent = desc;
  }

  function highlightChip(container, activeBtn) {
    const chips = container.querySelectorAll('.chip-btn');
    chips.forEach(function (btn) { btn.classList.remove('active'); });
    activeBtn.classList.add('active');
  }

  function clearActiveChips(container) {
    const chips = container.querySelectorAll('.chip-btn');
    chips.forEach(function (btn) { btn.classList.remove('active'); });
  }

  function updateGenerateButtonState() {
    const textTrimmed = elements.textInput.value.trim();
    const provider = getCurrentProvider();
    const isConfigured = provider && provider.configured;
    const isTextValid = textTrimmed.length > 0 && textTrimmed.length <= state.maxTextLength;

    if (state.isGenerating) {
      elements.generateBtn.disabled = true;
      elements.generateBtnText.textContent = '合成中...';
    } else {
      elements.generateBtn.disabled = !isConfigured || !isTextValid;
      elements.generateBtnText.textContent = '开始合成语音';
    }
  }

  function showConfigError(msg) {
    elements.providerNotice.style.display = 'flex';
    elements.providerNoticeTitle.textContent = '配置加载失败';
    elements.providerNoticeDesc.textContent = msg;
    elements.generateBtn.disabled = true;
  }

  // =========================================================================
  // 核心：语音合成请求处理 POST /api/tts
  // =========================================================================
  async function handleGenerate() {
    if (state.isGenerating) return;

    const provider = getCurrentProvider();
    if (!provider) {
      showError('CONFIG_ERROR', '未找到所选服务商配置。', '请刷新页面重新加载配置。');
      return;
    }

    if (!provider.configured) {
      showError('NOT_CONFIGURED', '所选引擎未配置有效 API 密钥。', '请在后端配置 GEMINI_API_KEY，或切换至 Edge 引擎。');
      return;
    }

    const textTrimmed = elements.textInput.value.trim();
    if (!textTrimmed) {
      showError('VALIDATION_ERROR', '合成文本不能为空。', '请输入要转换的文本后重试。');
      return;
    }

    if (textTrimmed.length > state.maxTextLength) {
      showError('TEXT_TOO_LONG', '文本超出限制：当前 ' + textTrimmed.length + ' 字，最大允许 ' + state.maxTextLength + ' 字。', '请缩减文本内容。');
      return;
    }

    // 构建符合 api-contract.md 的请求体
    const payload = {
      provider: provider.id,
      text: textTrimmed,
      voice: state.selectedVoiceId || provider.defaultVoice
    };

    // 仅在 Gemini 支持 Style 时附加 style 参数
    if (provider.supportsStyle) {
      const styleVal = elements.styleInput.value.trim();
      if (styleVal) {
        payload.style = styleVal.slice(0, 500);
      }
    }

    // 仅在 Edge 支持 Rate 时附加 rate 参数
    if (provider.supportsRate) {
      payload.rate = parseInt(elements.rateSlider.value, 10) || 0;
    }

    // 准备发起请求，切换加载中状态
    startGeneratingUI();

    // 创建 AbortController，并设置 150 秒超时
    const controller = new AbortController();
    state.abortController = controller;
    const timeoutId = setTimeout(function () {
      controller.abort();
    }, 150000);

    const startTime = performance.now();

    try {
      const response = await fetch('/api/tts', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload),
        signal: controller.signal
      });

      const elapsedMs = Math.round(performance.now() - startTime);

      if (response.ok) {
        // 200 成功响应：二进制流
        await handleSuccessResponse(response, payload, provider, elapsedMs);
      } else {
        // 非 200 失败响应：统一 JSON 格式
        await handleFailureResponse(response);
      }
    } catch (err) {
      if (err.name === 'AbortError') {
        showError('TIMEOUT_OR_CANCELLED', '请求已终止：超过 150 秒限时或已被手动取消。', '若网络较慢或文本较长，请稍后重试。');
      } else {
        showError('NETWORK_ERROR', '网络连接故障或服务不可达：' + (err.message || '未知错误'), '请检查后端服务是否正在运行在端口 8787。');
      }
      retainPreviousResultOnFailure();
    } finally {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      stopGeneratingUI();
    }
  }

  function handleCancel() {
    if (state.abortController) {
      state.abortController.abort();
    }
  }

  // =========================================================================
  // 成功处理 (200 Binary Stream)
  // =========================================================================
  async function handleSuccessResponse(response, requestPayload, provider, elapsedMs) {
    const contentType = response.headers.get('content-type') || (provider.id === 'gemini' ? 'audio/wav' : 'audio/mpeg');
    const contentDisposition = response.headers.get('content-disposition') || '';
    const respProvider = response.headers.get('x-tts-provider') || provider.name;
    const respModel = response.headers.get('x-tts-model') || provider.model;

    // 解析建议下载文件名
    let filename = provider.id === 'gemini' ? 'tts-gemini.wav' : 'tts-edge.mp3';
    if (contentDisposition) {
      const match = contentDisposition.match(/filename=["']?([^"';]+)["']?/i);
      if (match && match[1]) {
        filename = match[1].trim();
      }
    }

    // 获取音频二进制 Blob
    const blob = await response.blob();
    state.currentBlob = blob;

    // 释放旧 Object URL，防止浏览器内存泄漏
    if (state.currentAudioUrl) {
      URL.revokeObjectURL(state.currentAudioUrl);
    }
    const newAudioUrl = URL.createObjectURL(blob);
    state.currentAudioUrl = newAudioUrl;
    state.hasCurrentResult = true;

    // 隐藏错误面板
    elements.errorCard.style.display = 'none';

    // 格式化展示参数
    const isWav = contentType.includes('wav') || filename.endsWith('.wav');
    const formatName = isWav ? 'WAV (无损)' : 'MP3 (高压缩)';
    const fileSizeStr = formatBytes(blob.size);
    const durationStr = (elapsedMs / 1000).toFixed(1) + 's';
    const timeNow = new Date().toLocaleTimeString('zh-CN', { hour12: false });

    // 记录元数据
    state.previousAudioMeta = {
      provider: respProvider,
      model: respModel,
      format: formatName,
      size: fileSizeStr,
      duration: durationStr,
      time: timeNow,
      filename: filename,
      text: requestPayload.text
    };

    // 更新交付区域
    elements.resultBanner.className = 'result-banner banner-fresh';
    elements.resultStatusIcon.textContent = '✓';
    elements.resultStatusText.textContent = '合成成功';
    elements.resultTimestamp.textContent = timeNow;

    elements.metaProvider.textContent = respProvider;
    elements.metaFormat.textContent = formatName;
    elements.metaSize.textContent = fileSizeStr;
    elements.metaDuration.textContent = durationStr;

    // 更新音频播放器
    elements.audioPlayer.src = newAudioUrl;
    elements.audioPlayer.load();

    // 更新下载按钮
    elements.downloadBtn.href = newAudioUrl;
    elements.downloadBtn.download = filename;
    elements.downloadFormatLabel.textContent = isWav ? 'WAV' : 'MP3';

    // 切换视图到成功结果
    elements.stateIdle.style.display = 'none';
    elements.stateGenerating.style.display = 'none';
    elements.stateResult.style.display = 'flex';
    elements.deliveryBadge.textContent = '就绪 · 最新';
    elements.deliveryBadge.style.backgroundColor = 'var(--color-success-bg)';
    elements.deliveryBadge.style.color = 'var(--color-success)';

    // 优雅处理浏览器自动播放策略
    elements.autoplayNotice.style.display = 'none';
    const playPromise = elements.audioPlayer.play();
    if (playPromise !== undefined) {
      playPromise.catch(function () {
        // 现代浏览器拦截无交互自动播放，友好展示提示
        elements.autoplayNotice.style.display = 'flex';
      });
    }

    // 记录至历史会话（纯元数据，不持有 Blob URL 与操作控件）
    addToHistory({
      text: requestPayload.text,
      providerName: respProvider,
      time: timeNow
    });
  }

  // =========================================================================
  // 失败处理 (400, 413, 429, 502, 503, 504 JSON Error)
  // =========================================================================
  async function handleFailureResponse(response) {
    let errorCode = 'HTTP_' + response.status;
    let errorMessage = '语音合成请求未成功 (HTTP ' + response.status + ')';
    let errorAdvice = '请核对参数后重试。';

    try {
      const errorJson = await response.json();
      if (errorJson && errorJson.error) {
        errorCode = errorJson.error.code || errorCode;
        errorMessage = errorJson.error.message || errorMessage;
      }
    } catch (e) {
      // 响应体非 JSON，保留默认消息
    }

    // 根据 HTTP 状态码提供友好排查建议
    switch (response.status) {
      case 400:
        errorAdvice = '请求参数验证不通过，请检查所选发音人、风格或文本格式。';
        break;
      case 413:
        errorAdvice = '文本内容过长，超出了后端服务单次支持的最大长度限制。';
        break;
      case 429:
        errorAdvice = '上游服务频次超限或配额耗尽，请稍作等待后再次尝试。';
        break;
      case 502:
        errorAdvice = '上游语音合成服务返回异常，请确认网络连接是否通畅。';
        break;
      case 503:
        errorAdvice = 'Gemini API 密钥缺失或后端服务暂不可用，建议切换至 Edge 引擎。';
        break;
      case 504:
        errorAdvice = '后端请求上游语音引擎超时，请尝试缩短文本或重试。';
        break;
      default:
        errorAdvice = '请检查后端运行日志或稍后重试。';
    }

    showError(errorCode, errorMessage, errorAdvice);
    retainPreviousResultOnFailure();
  }

  /**
   * 规范严格要求：如果存在上一次成功的音频，失败后绝不销毁，
   * 而是保留播放器，并明确标识为“上一次合成结果 (当前请求失败)”
   */
  function retainPreviousResultOnFailure() {
    if (state.hasCurrentResult && state.currentAudioUrl && state.previousAudioMeta) {
      elements.stateIdle.style.display = 'none';
      elements.stateGenerating.style.display = 'none';
      elements.stateResult.style.display = 'flex';

      // 切换标识条为醒目的警告态
      elements.resultBanner.className = 'result-banner banner-previous';
      elements.resultStatusIcon.textContent = '⚠️';
      elements.resultStatusText.textContent = '上一次合成结果 (当前合成未成功)';
      elements.resultTimestamp.textContent = state.previousAudioMeta.time || '';

      elements.deliveryBadge.textContent = '保留前序结果';
      elements.deliveryBadge.style.backgroundColor = 'var(--color-warning-bg)';
      elements.deliveryBadge.style.color = 'var(--color-warning)';
    } else {
      // 若此前从未成功生成过，显示空闲状态
      elements.stateResult.style.display = 'none';
      elements.stateGenerating.style.display = 'none';
      elements.stateIdle.style.display = 'flex';
      elements.deliveryBadge.textContent = '未生成';
      elements.deliveryBadge.style.backgroundColor = 'var(--bg-muted)';
      elements.deliveryBadge.style.color = 'var(--ink-500)';
    }
  }

  // =========================================================================
  // 错误渲染 (严格文本节点，杜绝任何 XSS)
  // =========================================================================
  function showError(code, message, advice) {
    elements.errorCodeBadge.textContent = String(code);
    elements.errorMessage.textContent = String(message);
    elements.errorAdvice.textContent = String(advice || '');
    elements.errorCard.style.display = 'flex';

    // 滚动至错误卡片视野
    elements.errorCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  // =========================================================================
  // 加载与计时器状态控制
  // =========================================================================
  function startGeneratingUI() {
    state.isGenerating = true;
    updateGenerateButtonState();

    // 禁用关键输入
    elements.textInput.disabled = true;
    elements.voiceSelect.disabled = true;
    elements.rateSlider.disabled = true;
    elements.styleInput.disabled = true;

    // 显示取消按钮
    elements.cancelBtn.style.display = 'inline-flex';

    // 切换右侧视图为加载声波
    elements.stateIdle.style.display = 'none';
    elements.stateResult.style.display = 'none';
    elements.stateGenerating.style.display = 'flex';
    elements.autoplayNotice.style.display = 'none';

    elements.deliveryBadge.textContent = '合成中...';
    elements.deliveryBadge.style.backgroundColor = 'var(--accent-blue-light)';
    elements.deliveryBadge.style.color = 'var(--accent-blue)';

    // 启动秒表
    state.startTime = performance.now();
    elements.elapsedTime.textContent = '0.0';
    clearInterval(state.timerIntervalId);
    state.timerIntervalId = setInterval(function () {
      const elapsed = ((performance.now() - state.startTime) / 1000).toFixed(1);
      elements.elapsedTime.textContent = elapsed;
    }, 100);
  }

  function stopGeneratingUI() {
    state.isGenerating = false;
    clearInterval(state.timerIntervalId);
    state.abortController = null;

    // 恢复输入控件
    elements.textInput.disabled = false;
    elements.voiceSelect.disabled = false;
    elements.rateSlider.disabled = false;
    elements.styleInput.disabled = false;

    // 隐藏取消按钮
    elements.cancelBtn.style.display = 'none';

    updateGenerateButtonState();
  }

  // =========================================================================
  // 历史记录功能（纯元数据记录，不持有 Object URL，不提供历史重播/下载，避免过期链接）
  // =========================================================================
  function addToHistory(item) {
    state.historyList.unshift({
      text: item.text,
      providerName: item.providerName,
      time: item.time
    });
    if (state.historyList.length > 10) {
      state.historyList.pop();
    }
    renderHistoryList();
  }

  function renderHistoryList() {
    elements.historyList.innerHTML = '';
    if (state.historyList.length === 0) {
      const empty = document.createElement('div');
      empty.className = 'history-empty';
      empty.textContent = '暂无历史生成记录';
      elements.historyList.appendChild(empty);
      return;
    }

    state.historyList.forEach(function (item) {
      const row = document.createElement('div');
      row.className = 'history-item';

      const main = document.createElement('div');
      main.className = 'history-main';

      const text = document.createElement('span');
      text.className = 'history-text';
      text.textContent = item.text;
      main.appendChild(text);

      const sub = document.createElement('span');
      sub.className = 'history-sub';
      sub.textContent = item.providerName + ' · ' + item.time;
      main.appendChild(sub);

      row.appendChild(main);
      elements.historyList.appendChild(row);
    });
  }

  // =========================================================================
  // 工具函数
  // =========================================================================
  function formatBytes(bytes) {
    if (!bytes || bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  // 启动应用
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
