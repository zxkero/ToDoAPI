const API_URL = 'http://localhost:8080';

const taskList = document.getElementById('task-list');
const taskInput = document.getElementById('task-input');
const addTaskButton = document.getElementById('add-task-button');

async function fetchTasks() {
    taskList.innerHTML = '';

    const response = await fetch(`${API_URL}/tasks`);
    const tasks = await response.json();

    tasks.forEach(task => {
        const li = document.createElement('li');
        li.innerHTML = `
            <span>${task.title}</span>
            <button class="delete-btn" data-id="${task.id}">Удалить</button>
        `;
        taskList.appendChild(li);
    });
}

async function addTask() {
    const title = taskInput.value;
    if (!title) {
        alert('Название задачи не может быть пустым!');
        return;
    }

    await fetch(`${API_URL}/tasks`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ title: title }),
    });

    taskInput.value = '';
    await fetchTasks();
}

async function deleteTask(taskId) {
    await fetch(`${API_URL}/tasks/${taskId}`, {
        method: 'DELETE',
    });

    await fetchTasks();
}

document.addEventListener('DOMContentLoaded', fetchTasks);

addTaskButton.addEventListener('click', addTask);

taskList.addEventListener('click', (event) => {
    if (event.target.classList.contains('delete-btn')) {
        const taskId = event.target.getAttribute('data-id');
        deleteTask(taskId);
    }
});