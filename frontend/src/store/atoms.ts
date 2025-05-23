import { atom } from "jotai";
import type { UserResponse } from "../types/userTypes";


const userAtom = atom<UserResponse | null>(null);

const userFetchingStateAtom = atom<"fetching" | "error" | "completed">("fetching");

export { userAtom, userFetchingStateAtom };
