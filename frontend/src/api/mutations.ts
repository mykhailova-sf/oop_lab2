import { api } from "../config/api.config";
import type { ConsultationRequest, ConsultationResponse } from "../types/consultationTypes";
import type { Role, UserLogin, UserRequest } from "../types/userTypes";

// auth
export const postRegistration = (userData: UserRequest): Promise<void> =>
   api.post("/registration", userData);

export const postLogin = (userCredentials: UserLogin): Promise<void> =>
   api.post("/login", userCredentials);

export const postLogout = (): Promise<void> => api.post("/logout");

export const deleteAccount = (): Promise<void> => api.delete("/delete-account");

// code
export const generateCode = (role: Role): Promise<{ value: string }> =>
   api.post("/generate-code", { value: role }).then((res) => res.data);

export const validateCode = (
   code: string
): Promise<{ value: "valid" | "invalid" }> =>
   api.post("/validate-code", { value: code }).then((res) => res.data);

// consultations

export const postConsultation = (
   consultation: ConsultationRequest
): Promise<void> => api.post("/consultations", consultation);

type ConsultationPathRequest = Partial<ConsultationResponse>;

export const patchConsultation = (
   consultation: ConsultationPathRequest
): Promise<void> => api.patch("/consultations", consultation);
