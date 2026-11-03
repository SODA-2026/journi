import { describe, it, expect } from "vitest";
import { validate } from "./TripValidation";

describe("validate", () => {
    const valid = {
        title: "Paris",
        startDate: "2026-11-05",
        endDate: "2026-11-08",
    };

    it("returns no errors for valid input", () => {
        expect(validate(valid)).toEqual({})
    });

    // TODO: empty title gives errors.title
    it("rejects an empty title", () => {
        const result = validate({ ...valid, title: "" });
        expect(result.title).toBeDefined();
    });
    // TODO: title with only spaces gives errors.title
    it("rejects a title with only spaces", () => {
        const result = validate({ ...valid, title: "  " });
        expect(result.title).toBeDefined();
    })
    // TODO: missing start date gives errors.startDate
    it("rejects a missing start date", () => {
        const result = validate({...valid, startDate: ""})
        expect(result.startDate).toBeDefined()
    })
    // TODO: missing end date gives errors.endDate
    it("rejects a missing end date", () => {
        const result = validate({...valid, endDate: ""})
        expect(result.endDate).toBeDefined()
    })
    // TODO: end before start gives errors.endDate
    it("rejects and end date before start date", ()=> {
        const result = validate({
            ...valid,
            startDate: "2026-11-08",
            endDate: "2026-11-05" })
        expect(result.endDate).toBeDefined()
    })

    // TODO: same start and end date gives no error
    it("allows the same start and end date", () => {
        const result = validate({
            ...valid,
            startDate: "2026-11-05",
            endDate: "2026-11-05"
        })
        expect(result).toEqual({})
    })

});
