import type { UserResponse } from "./userTypes";

export type AppointmentRequest = {
   patientId: number;
   doctorId: number; // doctor who assigned
   appointmentType: "procedure" | "surgery";
   status: "pending";
};

export type AppointmentResponse = {
   id: number;
   patientDto: UserResponse;
   doctorDto: UserResponse; // doctor who assigned
   appointmentType: "procedure" | "surgery";
   status: "pending" | "declined" | "completed";
};

declare type ConsultationEntity = {
   id: number;
   patientId: number;
   doctorId: number;
   appointmentType: "procedure" | "surgery";
   status: "pending" | "declined" | "completed";
}
