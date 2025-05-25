import type { UserResponse } from "../types/userTypes";
import {  useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { postConsultation } from "../api/mutations";
import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import { Modal, useCloseModal } from "./theme/Modal";
import { DoctorIcon } from "../assets/icons";

function DoctorCard({ doctor }: { doctor: UserResponse }) {
   const { closeModalButtonRef, closeModal } = useCloseModal();

   const { mutateAsync: scheduleAsync, isPending } = useMutation({
      mutationFn: postConsultation
   });

   const [date, setDate] = useState("");

   const user = useAtomValue(userAtom);
   const schedule = async () => {
      await scheduleAsync({
         patientId: user?.id || 1,
         doctorId: doctor.id,
         status: "pending"
      });
      closeModal();
   };

   return (
      <div className="card p-4 border border-base-content/10 shadow">
         <div className="flex gap-4">
            <div className="avatar">
               <div className="w-12 rounded-xl">
                  <DoctorIcon />
               </div>
            </div>
            <div>
               <p className="text-base-content/70">
                  {doctor.firstName} {doctor.lastName}
               </p>
               <div>{doctor.doctorSpecialty}</div>
            </div>
            <div className="ml-auto">
               <Modal
                  trigger={
                     <button className="btn btn-secondary">
                        Schedule a consultation
                     </button>
                  }
                  content={
                     <>
                        <h3 className="font-bold text-xl text-center">
                           Schedule a consultations
                        </h3>
                        <div>
                           <div className="flex justify-center my-4 gap-4">
                              <span className="text-base-content/50">
                                 Select the date:{" "}
                              </span>
                              <input
                                 type="date"
                                 value={date}
                                 onChange={({ target }) =>
                                    setDate(target.value)
                                 }
                              />
                           </div>
                        </div>
                        <div className="flex gap-4 justify-center">
                           <form method="dialog">
                              <button ref={closeModalButtonRef} className="btn">
                                 Close
                              </button>
                           </form>
                           <button
                              className="btn btn-secondary"
                              onClick={schedule}
                              disabled={isPending}
                           >
                              {isPending ? "Submitting.." : "Submit"}
                           </button>
                        </div>
                     </>
                  }
               />
            </div>
         </div>
      </div>
   );
}

export { DoctorCard };
