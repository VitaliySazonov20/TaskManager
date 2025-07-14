document.addEventListener('DOMContentLoaded', function() {
    const modal = new bootstrap.Modal(document.getElementById('detailsModal'));

    const logsData = JSON.parse(document.getElementById('logs-data').textContent);

    // When any details button is clicked
    document.querySelectorAll('[data-bs-target="#detailsModal"]').forEach(btn => {
        btn.addEventListener('click', function() {
            const logId = this.getAttribute('data-log-id');
            const log = logsData.find(l => l.id == logId);

            // Update modal content
            document.getElementById('modalLogId').textContent = logId;
            document.getElementById('modalLogDetails').textContent =
                JSON.stringify(log.details, null, 2);

        });
    });



});

function copyToClipboard() {
        const detailsText = document.getElementById('modalLogDetails').textContent;
        navigator.clipboard.writeText(detailsText).then(() => {
            const copyBtn = document.querySelector('[onclick="copyToClipboard()"]');
            copyBtn.innerHTML = '<i class="bi bi-check"></i> Copied!';
            setTimeout(() => {
                copyBtn.innerHTML = '<i class="bi bi-clipboard"></i> Copy';
            }, 2000);
        });
    }