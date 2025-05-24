document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('comment-form');
    const list = document.getElementById('comment-list');

    if (form && list) {
        form.addEventListener('submit', async function (e){
            e.preventDefault();

            const rawData = new FormData(form);
            const formData = new URLSearchParams();

            for (const [key, value] of rawData.entries()) {
                formData.append(key, value.toString());
            }
            const response = await fetch('/post/comments', {
                method: 'POST',
                body: formData
            });
            if (response.ok) {
                const text = formData.get("content")?.toString();
                if (typeof text === "string") {
                    const commentId = await response.text();

                    const li = document.createElement("li");
                    li.setAttribute("data-id", commentId);
                    li.innerHTML = `<span class`
                    li.innerHTML = `<span class="comment-content">${text}</span><button class="edit-btn" type="button">Редактировать</button><button class="delete-btn" type="button">Удалить</button>`;
                    list.appendChild(li);
                    form.reset();

                    attachEditListeners();
                    attachDeleteListeners()
                }
            }
        });
    }
});