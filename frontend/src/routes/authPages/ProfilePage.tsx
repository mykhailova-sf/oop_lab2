import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/atoms";

import type { UserResponse } from "../../types/userTypes";
import { Separator } from "../../components/Separator";
import { GenerateCode } from "../../components/GenerateCode";
import { RoleBasedIcon } from "../../components/RoleBasedIcon";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { postLogout } from "../../api/mutations";
import { queryKeys } from "../../config/queryKeys";

function ProfilePage() {
   const user = useAtomValue(userAtom);

   if (user)
      return (
         <>
            <h1 className="text-center text-3xl font-bold">Profile</h1>
            <section className="flex max-w-[600px] mx-auto mt-4 gap-4">
               <div>
                  <div className="avatar">
                     <RoleBasedIcon className="w-24" />
                  </div>
                  <p className="card py-1 px-2 border border-base-content/10 text-center bg-base-200/20 shadow">
                     {user.role}
                  </p>
               </div>
               <div className="card p-4 border border-base-content/10 grow-1 shadow">
                  <p>
                     <span className="text-base-content/50">First name: </span>
                     {user.firstName}
                  </p>
                  <p>
                     <span className="text-base-content/50">Last name: </span>
                     {user.lastName}
                  </p>
                  <p className="text-base-content/50">
                     <span>email: </span>
                     <span className="italic text-base-content/80">
                        {user?.email || "yo"}
                     </span>
                  </p>
               </div>
            </section>
            <RoleBasedZone user={user} />
            <DangerZone user={user} />
         </>
      );
}

function RoleBasedZone({ user }: { user: UserResponse }) {
   if (user.role === "NURSE") return <></>;
   if (user.role === "ADMIN")
      return (
         <section className="max-w-[600px] mx-auto mt-8">
            <div className="flex gap-2 items-center">
               <h2 className="shrink-0 font-bold text-base-content/50">
                  Code generation
               </h2>
               <Separator />
            </div>
            <div className="p-4 space-y-2">
               <GenerateCode role="DOCTOR" />
               <GenerateCode role="NURSE" />
            </div>
         </section>
      );
   if (user.role === "DOCTOR")
      return (
         <section className="max-w-[600px] mx-auto mt-8">
            <div className="flex gap-2 items-center">
               <h2 className="shrink-0 font-bold text-base-content/50">
                  Doctor info
               </h2>
               <Separator />
            </div>
            <p className="card p-4 border border-base-content/10 shadow mt-2 flex flex-row gap-2">
               <span className="text-base-content/50">Specialty:</span>
               <span>
                  {user?.doctorSpecialty || "Your specialty is not defined"}
               </span>
            </p>
            <div className="p-4 space-y-2">
               <GenerateCode role="NURSE" />
            </div>
         </section>
      );
   if (user.role === "PATIENT")
      return (
         <section className="max-w-[600px] mx-auto mt-8">
            <div className="flex gap-2 items-center">
               <h2 className="shrink-0 font-bold text-base-content/50">
                  Patient info
               </h2>
               <Separator />
            </div>
            <p className="card p-4 border border-base-content/10 shadow mt-2 flex flex-row gap-2">
               <span className="text-base-content/50">Disgnosis:</span>
               <span>{user?.diagnosis || "No diagnosis yet"}</span>
            </p>
         </section>
      );
}

function DangerZone({ user: _ }: { user: UserResponse }) {
   const queryClient = useQueryClient();

   const setUser = useSetAtom(userAtom);

   const { mutate, isPending } = useMutation({
      mutationFn: postLogout,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: queryKeys.currentUser });
         setUser(null);
      }
   });

   return (
      <section className="max-w-[600px] mx-auto mt-8">
         <div className="flex gap-2 items-center">
            <h2 className="shrink-0 font-bold text-base-content/50">
               Caution zone
            </h2>
            <Separator />
         </div>
         <div className="space-y-2 mt-2 px-4">
            <div className="flex justify-between items-center">
               <span className="text-base-content/70">Change password</span>
               <button className="btn">Change password</button>
            </div>

            <div className="flex justify-between items-center">
               <span className="text-base-content/70">
                  Log out from the account
               </span>
               <button
                  className="btn"
                  onClick={() => mutate()}
                  disabled={isPending}
               >
                  {isPending ? "Logging out.." : "Logout"}
               </button>
            </div>

            <div className="flex justify-between items-center">
               <span className="text-base-content/70">Delete an account</span>
               <button className="btn btn-error">Delete</button>
            </div>
         </div>
      </section>
   );
}

export { ProfilePage };
