const test = require("node:test");
const assert = require("node:assert/strict");
const {
    buildChartModel,
    formatElapsed,
    groupSeatsByFloor,
    shiftIsoDate
} = require("../../main/resources/META-INF/resources/scripts/frontend-logic.js");

test("groups extra and non-contiguous catalog seats without placeholders", () => {
    const groups = groupSeatsByFloor([
        {id: 42, name: "Ruhezone", floor: "2OG", mapX: 0.4, mapY: 0.5},
        {id: 2, name: "Fenster", floor: "1OG", mapX: 0.2, mapY: 0.3},
        {id: 99, name: "Flexplatz", floor: "2OG", mapX: null, mapY: 2}
    ], ["1OG", "2OG", "3OG"]);

    assert.deepEqual(groups.map(group => group.floor), ["1OG", "2OG", "3OG"]);
    assert.deepEqual(groups.flatMap(group => group.seats.map(seat => seat.id)), [2, 42, 99]);
    assert.deepEqual(groups[1].placeable.map(seat => seat.id), [42]);
    assert.deepEqual(groups[1].unplaced.map(seat => seat.id), [99]);
    assert.deepEqual(groups[2].seats, []);
});

test("builds named percent series from explicit keys", () => {
    const model = buildChartModel([
        {seatId: 42, seatName: "Ruhezone", hour: "10:00", occupancy: 0.125},
        {seatId: 2, seatName: "Fenster", hour: "10:00", occupancy: 1},
        {seatId: 42, seatName: "Ruhezone", hour: "11:00", occupancy: 0.5},
        {seatId: 2, seatName: "Fenster", hour: "11:00", occupancy: 0}
    ]);

    assert.deepEqual(model.hours, ["10:00", "11:00"]);
    assert.deepEqual(model.seatSeries.map(series => [series.id, series.name]), [
        [2, "Fenster"],
        [42, "Ruhezone"]
    ]);
    assert.deepEqual(model.seatSeries[1].values, [12.5, 50]);
});

test("calculates elapsed time from the absolute instant without a timezone offset", () => {
    const start = "2026-10-02T10:00:00Z";
    assert.equal(formatElapsed(start, Date.parse("2026-10-02T10:00:05Z")), "5 s");
    assert.equal(formatElapsed(start, Date.parse("2026-10-02T09:59:59Z")), "0 s");
});

test("moves the selected chart date by exactly one local calendar day", () => {
    assert.equal(shiftIsoDate("2026-10-02", -1), "2026-10-01");
    assert.equal(shiftIsoDate("2026-10-31", 1), "2026-11-01");
    assert.equal(shiftIsoDate("2026-12-31", 1), "2027-01-01");
    assert.equal(shiftIsoDate("2028-02-28", 1), "2028-02-29");
});
