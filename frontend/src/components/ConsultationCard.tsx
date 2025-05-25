import type { ConsultationResponse } from "../types/consultationTypes";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import { StatusBar } from "./Status";
import { ConsultationIcon } from "../assets/icons";
import { declineConsultation } from "../api/mutations";
import { Modal } from "./theme/Modal";
import { CompleteConsultationForm } from "./CompleteConsultationForm";

function ConsultationCard({
   consultation
}: {
   consultation: ConsultationResponse;
}) {
   const user = useAtomValue(userAtom);

   const queryClinet = useQueryClient();

   const { mutateAsync: cancelConsultationAsync, isPending } = useMutation({
      mutationFn: declineConsultation,
      onSuccess: () => {
         queryClinet.invalidateQueries({
            queryKey: queryKeys.currentUserConsultations
         });
         queryClinet.invalidateQueries({ queryKey: queryKeys.consultations });
      }
   });

   if (!user) return <>No user</>;

   const canBeExamined = user.role === "DOCTOR";

   const canBeDeclined = consultation.status === "pending";

   const cancelConsultation = async () => {
      await cancelConsultationAsync(consultation.id);
   };

   return (
      <div className="card p-4 border border-base-content/10 shadow">
         <div className="flex gap-4">
            <div className="flex flex-col gap-1">
               <div className="flex gap-2">
                  <div className="w-8">
                     <ConsultationIcon />
                  </div>
                  <StatusBar status={consultation.status} />
               </div>
               <div>
                  <span className="text-base-content/50">Diagnosis: </span>
                  {consultation.diagnosis || "---"}
               </div>
               <div>
                  <span className="text-base-content/50">Prescription: </span>
                  {consultation.prescription || "---"}
               </div>
               <div className="grow" />
               <div className="flex gap-4">
                  {canBeDeclined && canBeExamined && (
                     <Modal
                        trigger={
                           <button className="btn btn-secondary">
                              Examine the patient
                           </button>
                        }
                        content={
                           <>
                              <CompleteConsultationForm
                                 consultation={consultation}
                              />
                           </>
                        }
                     />
                  )}
                  {canBeDeclined && (
                     <button
                        className="btn btn-error"
                        onClick={cancelConsultation}
                        disabled={isPending}
                     >
                        {isPending ? "Declining.." : "Decline"}
                     </button>
                  )}
               </div>
            </div>

            <div className="ml-auto space-y-4">
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Doctor - </span>
                     {consultation.doctorDto.firstName}{" "}
                     {consultation.doctorDto.lastName}
                  </div>
                  <button className="btn btn-sm">Doctor's infomation</button>
               </div>
               <div className="flex flex-col gap-1">
                  <div>
                     <span> Patient - </span>
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
