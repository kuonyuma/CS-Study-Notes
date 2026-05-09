// main.js - 林间回音 (Message Wall) 交互逻辑

document.addEventListener('DOMContentLoaded', () => {
    // 1. 动态加载后端留言数据
    const messageContainer = document.getElementById('message-container');
    let allMessages = [];

    if (messageContainer) {
        fetch('/api/messages')
            .then(res => res.json())
            .then(data => {
                allMessages = data;
                renderMessages(allMessages);
            })
            .catch(err => {
                console.error('获取留言失败:', err);
                messageContainer.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; color: var(--text-muted); padding: 2rem;">❌ 森林的信号不太好，获取回音失败了...请确认后端是否运行。</div>';
            });
    }

    function renderMessages(messagesToRender) {
        if (!messageContainer) return;
        messageContainer.innerHTML = '';
        
        if (messagesToRender.length === 0) {
            messageContainer.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; color: var(--text-muted); padding: 2rem;">🌲 森林里静悄悄的，还没有人留下回音。</div>';
            return;
        }

        messagesToRender.forEach(msg => {
            const card = document.createElement('div');
            // 将不同中文标签映射到简短的英文tag用于CSS过滤
            let tagClass = "all";
            if (msg.tag === "碎碎念") tagClass = "life";
            else if (msg.tag === "学业压力") tagClass = "study";
            else if (msg.tag === "心情日记") tagClass = "mood";

            card.className = 'message-card';
            card.setAttribute('data-tag', tagClass);
            
            // 重要：由于现在的卡片是 div 而不是 a 标签，需要手动添加点击跳转事件
            card.style.cursor = 'pointer';
            card.addEventListener('click', () => {
                window.location.href = `detail.html?id=${msg.id}`;
            });

            card.innerHTML = `
                <div class="message-header">
                    <span class="message-author">${msg.author}</span>
                    <span class="message-date">${msg.date}</span>
                </div>
                <div class="message-body">
                    ${msg.content}
                </div>
                <div class="message-footer">
                    <span class="message-tag">#${msg.tag}</span>
                    <span class="message-stats"><span class="material-icons" style="font-size: 14px;">favorite</span> ${msg.likes}</span>
                </div>
            `;
            messageContainer.appendChild(card);
        });
    }

    // 2. 留言列表页搜索功能
    const searchInput = document.getElementById('search-input');
    const tags = document.querySelectorAll('.tag');

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            filterMessages(searchTerm, 'all');
            tags.forEach(t => t.classList.remove('active'));
            document.querySelector('.tag[data-tag="all"]').classList.add('active');
        });
    }

    if (tags.length > 0) {
        tags.forEach(tag => {
            tag.addEventListener('click', () => {
                tags.forEach(t => t.classList.remove('active'));
                tag.classList.add('active');
                const selectedTag = tag.getAttribute('data-tag');
                if (searchInput) searchInput.value = '';
                filterMessages('', selectedTag);
            });
        });
    }

    function filterMessages(searchTerm, tagFilter) {
        const messageCards = document.querySelectorAll('.message-card');
        messageCards.forEach(card => {
            const bodyText = card.querySelector('.message-body').textContent.toLowerCase();
            const cardTag = card.getAttribute('data-tag');
            
            const matchesSearch = bodyText.includes(searchTerm);
            const matchesTag = tagFilter === 'all' || cardTag === tagFilter;

            if (matchesSearch && matchesTag) {
                card.style.display = 'flex';
                setTimeout(() => card.style.opacity = '1', 50);
            } else {
                card.style.opacity = '0';
                setTimeout(() => card.style.display = 'none', 300);
            }
        });
    }

    // 3. 详情页动态渲染与表单提交
    const replyForm = document.getElementById('reply-form');
    const articleTitle = document.querySelector('.article-title');
    let currentMsgId = null;
    
    // 如果当前是在详情页 (通过判断是否有特定元素)
    if (articleTitle) {
        const urlParams = new URLSearchParams(window.location.search);
        currentMsgId = urlParams.get('id');

        if (currentMsgId) {
            // 查看帖子详情模式
            fetch('/api/messages')
                .then(res => res.json())
                .then(data => {
                    const msg = data.find(m => m.id === currentMsgId);
                    if (msg) {
                        document.querySelector('.article-title').textContent = "森林回音";
                        document.querySelector('.article-meta').innerHTML = `
                            <span class="material-icons" style="font-size: 16px; vertical-align: middle;">person</span> ${msg.author} | 
                            <span class="material-icons" style="font-size: 16px; vertical-align: middle;">event</span> ${msg.date} |
                            <span class="message-tag" style="padding: 2px 6px; font-size: 12px; background-color: var(--secondary-color); color: var(--primary-color); border-radius: 8px;">#${msg.tag}</span>
                        `;
                        // 清除假数据和图片，只保留用户真实内容
                        document.querySelector('.article-body').innerHTML = `<p style="font-size: 1.2rem; margin-top: 1rem;">${msg.content}</p>`;
                        
                        // 渲染评论区
                        document.querySelector('.reply-section h3').textContent = "✉️ 留下你的评论";
                        document.querySelector('button[type="submit"]').textContent = "发送评论";
                        
                        const commentsSection = document.getElementById('comments-section');
                        const commentsContainer = document.getElementById('comments-container');
                        commentsSection.style.display = 'block';
                        
                        if (msg.comments && msg.comments.length > 0) {
                            let commentsHtml = '';
                            msg.comments.forEach(c => {
                                commentsHtml += `
                                    <div style="padding: 1rem 0; border-bottom: 1px dashed var(--secondary-color);">
                                        <div style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 0.5rem;">
                                            <strong style="color: var(--primary-color);">${c.author}</strong> · ${c.date}
                                        </div>
                                        <div style="font-size: 1rem;">${c.content}</div>
                                    </div>
                                `;
                            });
                            commentsContainer.innerHTML = commentsHtml;
                        } else {
                            commentsContainer.innerHTML = '<p style="color: var(--text-muted); font-size: 0.9rem;">还没有人评论，快来抢沙发吧！</p>';
                        }
                    }
                })
                .catch(err => console.error('获取留言详情失败:', err));
        } else {
             // 发布新贴模式
             document.querySelector('.article-title').textContent = "写下你想说的话吧";
             document.querySelector('.article-meta').style.display = 'none';
             document.querySelector('.article-body').innerHTML = '<p style="color: var(--text-muted); text-align: center;">请在下方的表单中填写您的内容并发布。</p>';
             document.querySelector('.reply-section h3').textContent = "✏️ 撰写留言";
             document.querySelector('button[type="submit"]').textContent = "发布留言";
             document.getElementById('comments-section').style.display = 'none';
        }
    }
    
    if (replyForm) {
        replyForm.addEventListener('submit', (e) => {
            e.preventDefault(); 
            
            const nameInput = document.getElementById('name').value.trim();
            const messageInput = document.getElementById('message').value.trim();
            
            if (!messageInput) {
                alert('🌲 请填写内容哦！');
                return;
            }

            if (currentMsgId) {
                // 发表评论逻辑
                const newComment = {
                    author: nameInput || "匿名路人",
                    content: messageInput
                };

                fetch(`/api/messages/${currentMsgId}/comments`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newComment)
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert('✨ 评论成功！');
                        window.location.reload(); // 刷新页面展示新评论
                    }
                })
                .catch(err => alert('❌ 评论失败了，请稍后再试。'));

            } else {
                // 发布新留言（主贴）逻辑
                const newMsg = {
                    author: nameInput || "匿名路人",
                    content: messageInput,
                    tag: "碎碎念"
                };

                fetch('/api/messages', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newMsg)
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert(`✨ 提交成功！谢谢你，${newMsg.author}，你的回音已在林间荡漾。`);
                        window.location.href = 'messages.html';
                    }
                })
                .catch(err => alert('❌ 提交失败了，请稍后再试。'));
            }
        });
    }
});