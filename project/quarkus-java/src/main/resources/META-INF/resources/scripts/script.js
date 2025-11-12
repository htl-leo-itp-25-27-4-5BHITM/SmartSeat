let tableBody = document.getElementById("table-body");

function getAllSeats() {
    let html = `
        <tr>
            <th>Seat ID</th>
            <th>Gescannt Am</th>
        </tr>
    `;
    fetch("/api/getAllEntries")
        .then((response) => { return response.json(); })
        .then((data) => {

            for (let i = 0; i < data.length; i++) {
                html += `
                    <tr>
                        <td>${data[i].seatId}</td>
                        <td>${data[i].scannedAt}</td>
                    </tr>
                `;
            }

            tableBody.innerHTML = html;
        })
        .catch((error) => {
            console.error('Fetch error:', error);
        });
}

setInterval(getAllSeats(), 5000);