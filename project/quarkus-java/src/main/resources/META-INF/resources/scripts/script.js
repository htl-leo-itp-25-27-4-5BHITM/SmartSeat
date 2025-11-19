
function loadFloor(floorNumber) {
    if(floorNumber == 1) {
        document.getElementById('k1').style.display = 'block';
        document.getElementById('k2').style.display = 'block';
        document.getElementById('k3').style.display = 'block';
        document.getElementById('k4').style.display = 'none';
        document.getElementById('k5').style.display = 'none';
    } else if(floorNumber == 2) {
        document.getElementById('k1').style.display = 'none';
        document.getElementById('k2').style.display = 'none';
        document.getElementById('k3').style.display = 'none';
        document.getElementById('k4').style.display = 'block';
        document.getElementById('k5').style.display = 'block';
    }
}
loadFloor(1);