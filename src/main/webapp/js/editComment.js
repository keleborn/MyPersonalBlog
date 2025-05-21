document.querySelectorAll('.edit-btn').forEach(button => {
   button.addEventListener('click', function () {
       const li = button.closest('li');
       const span = li.querySelector('.comment-content');
       const oldText = span.textContent;
       const textArea = document.createElement('textarea');
       textArea.value = oldText;
       textArea.className = 'comment-edit';

       span.replaceWith(textArea);
       textArea.focus();

       textArea.addEventListener('keydown', async function (e) {
           if (e.ctrlKey && e.key === 'Enter') {
               const newText = textArea.value;
               const commentId = li.getAttribute('data-id');

               const response = await fetch('/MyPersonalBlog/post/comments/edit', {
                   method: 'POST',
                   headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                   body: `id=${commentId}&content=${encodeURIComponent(newText)}`
               });

               if (response.ok) {
                   const updatedSpan = document.createElement('span');
                   updatedSpan.className = 'comment-content';
                   updatedSpan.textContent = newText;

                   textArea.replaceWith(updatedSpan);
               } else {
                   alert('Непредвиденная ошибка при сохранении коммента');
               }
           }
       });
   });
});