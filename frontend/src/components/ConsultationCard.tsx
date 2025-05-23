import { useRef } from "react";
import type { ConsultationResponse } from "../types/consultationTypes";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { patchConsultation } from "../api/mutations";
import { queryKeys } from "../config/queryKeys";
import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";

function ConsultationCard({
   consultation
}: {
   consultation: ConsultationResponse;
}) {
   const dialogRef = useRef<HTMLDialogElement | null>(null);

   const queryClinet = useQueryClient();

   const { mutateAsync: cancelConsultationAsync, isPending } = useMutation({
      mutationFn: patchConsultation,
      onSuccess: () => {
         queryClinet.invalidateQueries({
            queryKey: queryKeys.currentUserConsultations
         });
         queryClinet.invalidateQueries({ queryKey: queryKeys.consultations });
      }
   });

   const user = useAtomValue(userAtom);

   const cancelConsultation = async () => {
      await cancelConsultationAsync({ status: "declined" });
   };

   return (
      <div className="card p-4 border border-base-content/10 shadow">
         <div className="flex gap-4">
            <div className="flex flex-col gap-1">
               <div className="flex">
                  <p className="card py-0.5 px-4 border border-base-content/10 text-center bg-base-200/20">
                     {consultation.status}
                  </p>
               </div>
               <div>
                  <span>Diagnosis: </span>
                  {consultation.diagnosis || "---"}
               </div>
               <div>
                  <span>Prescription: </span>
                  {consultation.prescription || "---"}
               </div>
               <div className="grow" />
               <div className="flex gap-4">
                  {user?.role === "DOCTOR" && (
                     <button className="btn btn-secondary">
                        Examine the patient
                     </button>
                  )}
                  <button
                     className="btn btn-error"
                     onClick={cancelConsultation}
                     disabled={isPending}
                  >
                     {isPending ? "Declining.." : "Decline"}
                  </button>
               </div>
            </div>

            <div className="ml-auto space-y-4">
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Doctor's full name: </span>
                     {consultation.doctorDto.firstName}{" "}
                     {consultation.doctorDto.lastName}
                  </div>
                  <button className="btn btn-sm">Doctor's infomation</button>
               </div>
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Patient's full name: </span>
                     {consultation.patientDto.firstName}{" "}
                     {consultation.patientDto.lastName}
                  </div>
                  <button className="btn btn-sm">Patinet's infomation</button>
               </div>
            </div>
         </div>
      </div>
   );
}

export { ConsultationCard };
