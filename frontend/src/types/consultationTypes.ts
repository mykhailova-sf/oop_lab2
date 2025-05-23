import type { UserResponse } from "./userTypes";

export type ConsultationRequest = {
   patientId: number;
   doctorId: number;
   status: "pending";
};

export type ConsultationResponse = {
   id: number;
   patientDto: UserResponse;
   doctorDto: UserResponse;
   status: "pending" | "declined" | "completed";

   diagnosis: string;
   prescription: string;
};

declare type ConsultationEntity = {
   id: number;
   patientId: number;
   doctorId: number;
   status: "pending" | "declined" | "completed";

   diagnosis: string;
   prescription: string;
}