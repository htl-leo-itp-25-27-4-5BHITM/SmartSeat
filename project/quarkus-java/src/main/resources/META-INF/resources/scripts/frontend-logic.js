(function exposeSmartSeatLogic(root) {
    function canonicalState(seat) {
        if (["FREE", "OCCUPIED", "UNKNOWN"].includes(seat.state)) return seat.state;
        if (seat.status === true) return "FREE";
        if (seat.status === false) return "OCCUPIED";
        return "UNKNOWN";
    }

    function stateLabel(state) {
        return {FREE: "Frei", OCCUPIED: "Besetzt", UNKNOWN: "Unbekannt"}[state] || "Unbekannt";
    }

    function displayFloor(floor) {
        const match = String(floor ?? "Unbekannt").match(/^(\d+)\.?OG$/i);
        return match ? `${match[1]}.OG` : String(floor ?? "Unbekannt");
    }

    function floorSort(a, b) {
        return displayFloor(a).localeCompare(displayFloor(b), "de", {numeric: true});
    }

    function validCoordinate(value) {
        return Number.isFinite(Number(value)) && Number(value) >= 0 && Number(value) <= 1;
    }

    function formatElapsed(occupiedSince, now = Date.now()) {
        if (!occupiedSince) return "Gerade eben";
        const since = Date.parse(occupiedSince);
        if (!Number.isFinite(since)) return "Zeitpunkt nicht verfügbar";
        const totalSeconds = Math.max(0, Math.floor((now - since) / 1000));
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;
        if (hours > 0) return `${hours} h ${minutes} min ${seconds} s`;
        if (minutes > 0) return `${minutes} min ${seconds} s`;
        return `${seconds} s`;
    }

    function shiftIsoDate(isoDate, days) {
        const match = String(isoDate).match(/^(\d{4})-(\d{2})-(\d{2})$/);
        if (!match || !Number.isInteger(days)) return isoDate;
        const [, year, month, day] = match.map(Number);
        const date = new Date(year, month - 1, day, 12);
        if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
            return isoDate;
        }
        date.setDate(date.getDate() + days);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
    }

    function groupSeatsByFloor(seats, configuredFloors = []) {
        const grouped = new Map();
        configuredFloors.forEach(floor => grouped.set(floor, []));
        seats.forEach(seat => {
            const floor = seat.floor || "Unbekannt";
            if (!grouped.has(floor)) grouped.set(floor, []);
            grouped.get(floor).push(seat);
        });
        return [...grouped.keys()].sort(floorSort).map(floor => {
            const floorSeats = grouped.get(floor).sort((a, b) => Number(a.id) - Number(b.id));
            const placeable = floorSeats.filter(seat =>
                validCoordinate(seat.mapX) && validCoordinate(seat.mapY));
            return {
                floor,
                seats: floorSeats,
                placeable,
                unplaced: floorSeats.filter(seat => !placeable.includes(seat))
            };
        });
    }

    function buildChartModel(data) {
        const hours = [...new Set(data.map(item => item.hour))].sort();
        const catalog = new Map();
        data.forEach(item => catalog.set(Number(item.seatId), item.seatName));
        return {
            hours,
            seatSeries: [...catalog.entries()]
                .sort(([left], [right]) => left - right)
                .map(([id, name]) => ({
                    id,
                    name,
                    values: hours.map(hour => {
                        const value = data.find(item => Number(item.seatId) === id && item.hour === hour);
                        return Number(value?.occupancy || 0) * 100;
                    })
                }))
        };
    }

    const api = {
        canonicalState,
        stateLabel,
        displayFloor,
        floorSort,
        validCoordinate,
        formatElapsed,
        shiftIsoDate,
        groupSeatsByFloor,
        buildChartModel
    };
    root.SmartSeatLogic = api;
    if (typeof module !== "undefined" && module.exports) module.exports = api;
})(typeof globalThis !== "undefined" ? globalThis : this);
