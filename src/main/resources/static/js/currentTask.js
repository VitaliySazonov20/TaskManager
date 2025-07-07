document.getElementById('post-comment-btn').addEventListener('click', async () => {
    const message = document.getElementById('comment-input');
    const taskId =document.getElementById('comment-input').getAttribute('data-task-id');
    if(!message) return;

    try{
        const response = await fetch(`/api/tasks/${taskId}/comments`,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: message.value.trim()

        });

        if(response.ok){
//            console.log("Comment posted :^) ");
            await refreshComments(taskId);
            message.value = '';
        }
    } catch (error){
        console.error('Error:', error);
    }
});

document.addEventListener('click', async function(event) {
    if(event.target.classList.contains('dropdown-item')){
        const taskId = event.target.dataset.taskId;
        const priority = event.target.dataset.priority;
        updatePriority(taskId,priority);
    }
})

async function updatePriority(taskId,priority){
    try{
        const response = await fetch(`/api/tasks/${taskId}/priority`,{
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: `${priority}`
        });
        if(response.ok){
//            console.log("changed priority");
            await updatePriorityBadge(taskId);
        }

    }catch (error){
             console.error('Error:', error);
         }
}

async function updatePriorityBadge(taskId){
    const response = await fetch(`/badgeContainer/fragment?taskId=${taskId}`);
    const html = await response.text();
    document.getElementById('badge-container').outerHTML = html;
}

async function refreshComments(taskId){
    const response = await fetch(`/comments/fragment?taskId=${taskId}`);
    const html = await response.text();
    document.getElementById('comments-container').outerHTML = html;
}