import { UserIcon } from "@heroicons/react/24/outline";
import type { UserResponse } from "../types/userTypes";
import { useRef } from "react";
import { useMutation } from "@tanstack/react-query";
import { postConsultation } from "../api/mutations";
import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";

function DoctorCard({ doctor }: { doctor: UserResponse }) {
   const dialogRef = useRef<HTMLDialogElement | null>(null);

   const { mutate: scheduleAsync, isPending } = useMutation({
      mutationFn: postConsultation
   });

   const user = useAtomValue(userAtom);

   const schedule = async () => {
      scheduleAsync({
         patientId: user?.id || 1,
         doctorId: doctor.id,
         status: "pending"
      });
   };

   return (
      <div className="card p-4 border border-base-content/10 shadow">
         <div className="flex gap-4">
            <div className="avatar">
               <div className="w-12 rounded-xl">
                  <UserIcon className="w-full" />
               </div>
            </div>
            <div>
               <p className="text-base-content/70">
                  {doctor.firstName} {doctor.lastName}
               </p>
               <div>{doctor.doctorSpecialty}</div>
            </div>
            <div className="ml-auto">
               <button
                  className="btn btn-secondary"
                  onClick={() =>
                     dialogRef.current && dialogRef.current.showModal()
                  }
               >
                  Schedule a consultation
               </button>
               <dialog ref={dialogRef} className="modal">
                  <div className="modal-box">
                     <h3 className="font-bold text-xl text-center">
                        Some scheduling form
                     </h3>
                     <p className=" text-center">
                        Lorem ipsum dolor sit amet, consectetur adipisicing
                        elit. Aperiam ratione ea fugit minus iusto id iure
                     </p>
                     <div className="flex gap-4 justify-end">
                        <form method="dialog">
                           <button className="btn">Close</button>
                        </form>
                        <button
                           className="btn btn-secondary"
                           onClick={schedule}
                           disabled={isPending}
                        >
                           {isPending ? "Submitting.." : "Submit"}
                        </button>
                     </div>
                  </div>
               </dialog>
            </div>
         </div>
      </div>
   );
}

export { DoctorCard };
