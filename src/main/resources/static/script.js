const API_URL = 'http://localhost:8080';

// Находим элементы в DOM
const taskList = document.getElementById('task-list');
const titleInput = document.getElementById('task-title-input');
const descriptionInput = document.getElementById('task-description-input');
const addTaskButton = document.getElementById('add-task-button');

/**
 * Загружает и отображает все задачи с сервера.
 */
async function fetchTasks() {
    taskList.innerHTML = ''; // Очищаем старый список

    try {
        const response = await fetch(`${API_URL}/tasks`);
        const tasks = await response.json();

        // Сортируем задачи по ID в обратном порядке, чтобы новые были сверху
        tasks.sort((a, b) => b.id - a.id);

        tasks.forEach(task => {
            const li = document.createElement('li');
            li.className = 'task-item'; // Добавляем класс для стилизации

            // Проверяем, есть ли описание, и если нет - не отображаем параграф
            const descriptionHtml = task.description
                ? `<p class="task-description">${task.description}</p>`
                : '';

            li.innerHTML = `
                <div class="task-content">
                    <div class="task-header">
                        <span class="task-title">${task.title}</span>
                        <span class="task-time">${task.creationTime}</span>
                    </div>
                    ${descriptionHtml}
                </div>
                <button class="delete-btn" data-id="${task.id}" aria-label="Удалить задачу">
                    <span class="material-symbols-outlined">delete</span>
                </button>
            `;
            taskList.appendChild(li);
        });
    } catch (error) {
        console.error("Ошибка при загрузке задач:", error);
        taskList.innerHTML = '<li class="task-item">Не удалось загрузить задачи.</li>';
    }
}

/**
 * Добавляет новую задачу.
 */
async function addTask() {
    const title = titleInput.value.trim();
    const description = descriptionInput.value.trim();

    if (!title) {
        alert('Название задачи не может быть пустым!');
        return;
    }

    try {
        await fetch(`${API_URL}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, description }),
        });

        // Очищаем поля ввода и обновляем список
        titleInput.value = '';
        descriptionInput.value = '';
        // Убираем фокус, чтобы метки вернулись в исходное положение
        titleInput.blur();
        descriptionInput.blur();

        await fetchTasks();
    } catch (error) {
        console.error("Ошибка при добавлении задачи:", error);
        alert("Не удалось добавить задачу.");
    }
}

/**
 * Удаляет задачу по ID.
 * @param {number} taskId - ID задачи для удаления.
 */
async function deleteTask(taskId) {
    try {
        await fetch(`${API_URL}/tasks/${taskId}`, {
            method: 'DELETE',
        });
        await fetchTasks(); // Обновляем список после удаления
    } catch (error) {
        console.error("Ошибка при удалении задачи:", error);
        alert("Не удалось удалить задачу.");
    }
}

// Первоначальная загрузка задач при загрузке страницы
document.addEventListener('DOMContentLoaded', fetchTasks);

// Обработчик для кнопки добавления
addTaskButton.addEventListener('click', addTask);

// Обработчик для кнопок удаления (используем делегирование событий)
taskList.addEventListener('click', (event) => {
    // Ищем кнопку, даже если клик был по иконке внутри нее
    const deleteButton = event.target.closest('.delete-btn');
    if (deleteButton) {
        const taskId = deleteButton.getAttribute('data-id');
        deleteTask(taskId);
    }
});