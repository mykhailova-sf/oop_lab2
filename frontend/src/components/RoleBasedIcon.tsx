import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import { UserIcon } from "@heroicons/react/24/outline";
import { DoctorIcon, NurseIcon } from "../assets/icons";

function RoleBasedIcon({ className }: { className: string }) {
   const user = useAtomValue(userAtom);

   if (!user) return <>No icon</>;
   return (
      <>
         <div className={className}>
            {["PATIENT", "ADMIN"].includes(user.role) && (
               <UserIcon className="w-full" />
            )}
            {user.role === "DOCTOR" && <DoctorIcon />}
            {user.role === "NURSE" && <NurseIcon />}
         </div>
      </>
   );
}

export { RoleBasedIcon };
