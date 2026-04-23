const boardElement = document.getElementById('board');
const cells = document.querySelectorAll('.cell');
const statusElement = document.getElementById('status');
const restartBtn = document.getElementById('restartBtn');

const PLAYER = 'X';
const AI = 'O';
let gameActive = true;

// Initialize Game
function initGame() {
    cells.forEach(cell => {
        cell.addEventListener('click', handleCellClick);
        cell.classList.remove('x', 'o');
        cell.innerText = '';
    });
    restartBtn.addEventListener('click', restartGame);
    statusElement.innerText = `轮到你 (${PLAYER})`;
    // Notify server to reset
    fetch('/api/restart');
}

function handleCellClick(event) {
    const clickedCell = event.target;
    const index = parseInt(clickedCell.getAttribute('data-index'));

    if (clickedCell.innerText !== '' || !gameActive) {
        return;
    }

    // 1. Update UI immediately for player
    updateCell(clickedCell, PLAYER);
    statusElement.innerText = "AI 思考中...";
    gameActive = false; // Prevent clicks while waiting

    // 2. Send move to server
    const row = Math.floor(index / 3);
    const col = index % 3;

    fetch(`/api/move?row=${row}&col=${col}`)
        .then(response => response.json())
        .then(data => {
            // Check immediate win/draw after player move
            if (data.winner === 'PLAYER') {
                endGame("你赢了！");
                return;
            }
            if (data.winner === 'DRAW') {
                endGame("平局！");
                return;
            }

            // 3. Update UI for AI move
            const aiIndex = data.aiRow * 3 + data.aiCol;
            const aiCell = cells[aiIndex];
            updateCell(aiCell, AI);

            // 4. Check result after AI move
            if (data.winner === 'AI') {
                endGame("AI 赢了！");
            } else if (data.winner === 'DRAW') {
                endGame("平局！");
            } else {
                gameActive = true;
                statusElement.innerText = `轮到你 (${PLAYER})`;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            statusElement.innerText = "服务器连接错误";
        });
}

function updateCell(cell, player) {
    cell.innerText = player;
    cell.classList.add(player.toLowerCase());
}

function endGame(message) {
    gameActive = false;
    statusElement.innerText = message;
}

function restartGame() {
    initGame();
    gameActive = true;
}

initGame();
