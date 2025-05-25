export type Role = "PATIENT" | "NURSE" | "DOCTOR" | "ADMIN";

// Creating a user
export type UserRequest = {
   firstName: string;
   lastName: string;
   email: string;
   password: string;

   specialCode?: string;
   role: Role;
   doctorSpecialty?: string;
};

// Loging into an account
export type UserLogin = {
   email: string;
   password: string;
};

export type UserResponse = {
   id: number;
   firstName: string;
   lastName: string;
   email: string;
   role: Role;

   doctorSpecialty?: string; // if that's a doctor

   diagnosis?: string; // patient receives after consultation
};

declare type UserEntity = {
   id: number;
   firstName: string;
   lastName: string;
   email: string;
   password: string;

   role: Role;
   doctorSpecialty?: string; // if that's a doctor

   diagnosis: string; // patient receives after consultation
};
