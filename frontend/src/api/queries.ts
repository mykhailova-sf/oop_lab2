import { api } from "../config/api.config";
import type { AppointmentResponse } from "../types/appointmentTypes";
import type { ConsultationResponse } from "../types/consultationTypes";
import type { UserResponse } from "../types/userTypes";

// current-user
export const getCurrentUser = (): Promise<UserResponse> =>
   api.get("/current-user").then((res) => res.data);

getCurrentUser.consultations = (): Promise<ConsultationResponse[]> =>
   api.get("/current-user/consultations").then((res) => res.data);

getCurrentUser.procedures = (): Promise<AppointmentResponse[]> =>
   api.get("/current-user/appointments?type=procedure").then((res) => res.data);

getCurrentUser.surgeries = (): Promise<AppointmentResponse[]> =>
   api.get("/current-user/appointments?type=surgery").then((res) => res.data);

// users
export const getUsers = (): Promise<UserResponse[]> =>
   api.get("/users").then((res) => res.data);

getUsers.byId = (id: number): Promise<UserResponse> =>
   api.get(`/users/${id}`).then((res) => res.data);

getUsers.doctors = (): Promise<UserResponse[]> =>
   api.get("/users?role=DOCTOR").then((res) => res.data);

// appointments and consultations
export const getConsultations = (): Promise<ConsultationResponse[]> =>
   api.get("/consultations").then((res) => res.data);

export const getAppointments = (): Promise<AppointmentResponse[]> =>
   api.get("/appointments").then((res) => res.data);

export const getProcedures = (): Promise<AppointmentResponse[]> =>
   api.get("/appointments?type=procedure").then((res) => res.data);

export const getSurgeries = (): Promise<AppointmentResponse[]> =>
   api.get("/appointments?type=surgery").then((res) => res.data);
