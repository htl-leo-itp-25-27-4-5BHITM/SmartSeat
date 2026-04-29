async function getAllSeats() {
    try {
        const res = await fetch(`/api/seat/getAllSeats`);
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}
getAllSeats().then(seats => {
    console.log(seats);
});