import { useMutation, useQueryClient } from "@tanstack/react-query";
import { declineAppointment, executeAppointment } from "../api/mutations";
import { queryKeys } from "../config/queryKeys";
import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import type { AppointmentResponse } from "../types/appointmentTypes";
import { StatusBar } from "./Status";
import { ProcedureIcon, SurgeryIcon } from "../assets/icons";

function AppointmentCard({
   appointment
}: {
   appointment: AppointmentResponse;
}) {
   const queryClinet = useQueryClient();

   const user = useAtomValue(userAtom);

   if (!user) return <></>;

   const canBeExamined =
      user.role === "DOCTOR" ||
      (user.role === "NURSE" && appointment.appointmentType === "procedure");

   const canBeDeclined = appointment.status === "pending";

   const { mutateAsync: executeAppointmentAsync, isPending: isExecuting } =
      useMutation({
         mutationFn: executeAppointment,
         onSuccess: () => {
            queryClinet.invalidateQueries({
               queryKey: queryKeys.currentUser
            });
            queryClinet.invalidateQueries({
               queryKey: queryKeys.procedures
            });
            queryClinet.invalidateQueries({
               queryKey: queryKeys.surgeries
            });
         }
      });

   const executeAppointmentHandler = async () => {
      await executeAppointmentAsync(appointment.id);
   };

   const { mutateAsync: cancelAppointmentAsync, isPending: isDeclining } =
      useMutation({
         mutationFn: declineAppointment,
         onSuccess: () => {
            queryClinet.invalidateQueries({
               queryKey: queryKeys.currentUserConsultations
            });
            queryClinet.invalidateQueries({
               queryKey: queryKeys.consultations
            });
         }
      });

   const cancelAppointmentHandler = async () => {
      await cancelAppointmentAsync(appointment.id);
   };

   return (
      <div className="card p-4 border border-base-content/10 shadow">
         <div className="flex gap-4">
            <div className="flex flex-col gap-1">
               <div className="flex gap-2">
                  {appointment.appointmentType === "surgery" ? (
                     <div className="w-8">
                        <SurgeryIcon />
                     </div>
                  ) : (
                     <div className="w-8">
                        <ProcedureIcon />
                     </div>
                  )}
                  <StatusBar status={appointment.status} />
               </div>
               <div>
                  <span className="text-base-content/50">
                     Appointment type:{" "}
                  </span>
                  {appointment.appointmentType === "surgery"
                     ? "Surgery"
                     : "Procedure"}
               </div>
               <div className="grow" />
               <div className="flex gap-4">
                  {canBeDeclined && canBeExamined && (
                     <button
                        className="btn btn-secondary"
                        onClick={executeAppointmentHandler}
                        disabled={isExecuting}
                     >
                        {isExecuting
                           ? "Executing.."
                           : `Execute ${appointment.appointmentType}`}
                     </button>
                  )}
                  {canBeDeclined && (
                     <button
                        className="btn btn-error"
                        onClick={cancelAppointmentHandler}
                        disabled={isDeclining}
                     >
                        {isDeclining ? "Declining.." : "Decline"}
                     </button>
                  )}
               </div>
            </div>

            <div className="ml-auto space-y-4">
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Doctor - </span>
                     {appointment.doctorDto.firstName}{" "}
                     {appointment.doctorDto.lastName}
                  </div>
                  <button className="btn btn-sm">Doctor's infomation</button>
               </div>
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Patient - </span>
                     {appointment.patientDto.firstName}{" "}
                     {appointment.patientDto.lastName}
                  </div>
                  <button className="btn btn-sm">Patinet's infomation</button>
               </div>
            </div>
         </div>
      </div>
   );
}

export { AppointmentCard };
