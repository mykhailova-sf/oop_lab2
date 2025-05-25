import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { completeConsultation, postAppointment } from "../api/mutations";
import type { ConsultationResponse } from "../types/consultationTypes";
import { useQueryClient } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { useCloseModal } from "./theme/Modal";
import { useState } from "react";
import { ProcedureIcon, SurgeryIcon } from "../assets/icons";
import type { AppointmentResponse } from "../types/appointmentTypes";
import { XCircleIcon } from "@heroicons/react/24/outline";

// Define Zod schema
const completeConsultationSchema = z.object({
   diagnosis: z.string().min(1, "Diagnosis is required"),
   prescription: z.string().min(1, "Prescription is required")
});

// Infer TypeScript type from Zod schema
type CompleteConsultationRequest = z.infer<typeof completeConsultationSchema>;

export function CompleteConsultationForm({
   consultation
}: {
   consultation: ConsultationResponse;
}) {
   const {
      register,
      handleSubmit,
      formState: { errors, isSubmitting }
   } = useForm<CompleteConsultationRequest>({
      resolver: zodResolver(completeConsultationSchema)
   });

   const [appointments, setAppointments] = useState<
      AppointmentResponse["appointmentType"][]
   >([]);

   const queryClient = useQueryClient();

   const [isAddAppointment, setIsAddAppointment] = useState(false);

   const { closeModalButtonRef, closeModal } = useCloseModal();

   const onSubmit = async (data: CompleteConsultationRequest) => {
      await completeConsultation({
         id: consultation.id,
         status: "completed",
         ...data
      });

      queryClient.invalidateQueries({
         queryKey: queryKeys.currentUserConsultations
      });
      queryClient.invalidateQueries({ queryKey: queryKeys.consultations });

      appointments.forEach(async (app) => {
         await postAppointment({
            patientId: consultation.patientDto.id,
            doctorId: consultation.doctorDto.id,
            appointmentType: app,
            status: "pending"
         });
      });

      queryClient.invalidateQueries({queryKey: queryKeys.surgeries});
      queryClient.invalidateQueries({queryKey: queryKeys.procedures});

      
      closeModal();

   };

   return (
      <>
         <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
               <label className="block font-medium">Diagnosis</label>
               <textarea
                  {...register("diagnosis")}
                  className="textarea w-full resize-none"
               />
               {errors.diagnosis && (
                  <p className="text-red-500">{errors.diagnosis.message}</p>
               )}
            </div>

            <div>
               <label className="block font-medium">Prescription</label>
               <textarea
                  {...register("prescription")}
                  className="textarea w-full resize-none"
               />
               {errors.prescription && (
                  <p className="text-red-500">{errors.prescription.message}</p>
               )}
            </div>
         </form>
         <div className="p-4">
            <div className="flex gap-2 mb-2 flex-wrap">
               {appointments.map((app, i) => (
                  <button
                     key={i}
                     className="btn"
                     onClick={() =>
                        setAppointments((aps) => [
                           ...aps.slice(0, i),
                           ...aps.slice(i + 1)
                        ])
                     }
                  >
                     {app} <XCircleIcon className="size-5" />
                  </button>
               ))}
            </div>
            {!isAddAppointment && (
               <button
                  className="btn btn-accent"
                  onClick={() => setIsAddAppointment(true)}
               >
                  + Add Appointment
               </button>
            )}
            {isAddAppointment && (
               <AddAppointmentForm
                  {...{ setAppointments, setIsAddAppointment }}
               />
            )}
         </div>
         <div className="flex gap-4 justify-center">
            <form method="dialog">
               <button ref={closeModalButtonRef} className="btn">
                  Close
               </button>
            </form>
            <button
               className="btn btn-secondary"
               disabled={isSubmitting}
               onClick={handleSubmit(onSubmit)}
            >
               {isSubmitting ? "Submitting.." : "Submit"}
            </button>
         </div>
      </>
   );
}

function AddAppointmentForm({
   setAppointments,
   setIsAddAppointment
}: {
   setAppointments: React.Dispatch<
      React.SetStateAction<("procedure" | "surgery")[]>
   >;
   setIsAddAppointment: (val: boolean) => void;
}) {
   const [appointmentType, setAppointmentType] =
      useState<AppointmentResponse["appointmentType"]>("procedure");

   const addAppointment = () => {
      setAppointments((aps) => [appointmentType, ...aps]);
      setIsAddAppointment(false);
   };
   return (
      <div className="flex gap-4 items-center">
         <select
            className="select"
            value={appointmentType}
            onChange={({ target }) => {
               if (target.value === "procedure" || target.value === "surgery")
                  setAppointmentType(target.value);
            }}
         >
            <option value="surgery">
               Surgery <SurgeryIcon />
            </option>
            <option value="procedure">Procedure</option>
         </select>
         <div className="w-8">
            {appointmentType === "surgery" && <SurgeryIcon />}
            {appointmentType === "procedure" && <ProcedureIcon />}
         </div>
         <button className="btn btn-accent" onClick={addAppointment}>
            + Add
         </button>
      </div>
   );
}
