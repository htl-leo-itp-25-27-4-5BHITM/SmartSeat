
function loadFloor(floorNumber) {
    if (floorNumber == 1) {
        document.getElementById('k1').style.display = 'block';
        document.getElementById('k2').style.display = 'block';
        document.getElementById('k3').style.display = 'block';
        document.getElementById('k4').style.display = 'none';
        document.getElementById('k5').style.display = 'none';
    } else if (floorNumber == 2) {
        document.getElementById('k1').style.display = 'none';
        document.getElementById('k2').style.display = 'none';
        document.getElementById('k3').style.display = 'none';
        document.getElementById('k4').style.display = 'block';
        document.getElementById('k5').style.display = 'block';
    }
}
loadFloor(1);

function getSeatsFrom1OG() {
    fetch('/api/seat/getSeatsByFloor/1OG')
        .then(res => res.json())
        .then(data => console.log(data))
        .catch(err => console.error(err));
}
getSeatsFrom1OG();

function getUnoccupiedCount() {
    fetch('/api/seat/getUnoccupiedSeatsByFloor/1OG')
        .then(res => res.json())
        .then(data => console.log(data))
        .catch(err => console.error(err));
}
getUnoccupiedCount();