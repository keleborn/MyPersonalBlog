function attachDeleteListeners() {
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', async function () {
            const li = button.closest('li');
            const commentId = li.getAttribute('data-id');
            const response = await fetch('/post/comments/delete', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `id=${commentId}`
            });

            if (response.ok) {
                li.remove();
            } else {
                alert('Непредвиденная ошибка при удалении коммента');
            }
        });
    });
}

attachDeleteListeners();