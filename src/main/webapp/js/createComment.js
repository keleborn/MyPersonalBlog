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
            const response = await fetch('/MyPersonalBlog/post/comments', {
                method: 'POST',
                body: formData
            });
            if (response.ok) {
                const text = formData.get("content")?.toString();
                if (typeof text === "string") {
                    const li = document.createElement("li");
                    li.innerText = text;
                    list.appendChild(li);
                    form.reset()
                }
            }
        });
    }
});