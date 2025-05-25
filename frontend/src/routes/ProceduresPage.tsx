import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { getCurrentUser, getProcedures } from "../api/queries";
import { AppointmentCard } from "../components/AppointmentCard";

function ProceduresPage() {
   const user = useAtomValue(userAtom);

   if (!user) return <>User is not Defined</>;

   return (
      <>
         <h1 className="text-center text-3xl font-bold">
            {user?.role === "PATIENT" ? "Your Procedures" : "Procedures"}
         </h1>
         <section className="max-w-[650px] mx-auto mt-4">
            <ProceduresProxy role={user?.role} />
         </section>
      </>
   );
}

function ProceduresProxy({ role }: { role: string }) {
   if (role === "PATIENT") return <ProceduresPatientData />;
   return <ProceduresData />;
}

function ProceduresPatientData() {
   const { data: proceures, isPending } = useQuery({
      queryKey: queryKeys.currentUserProcedures,
      queryFn: getCurrentUser.procedures
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );
   if (!isPending && proceures?.length === 0) {
      <>No data</>;
   }
   if (proceures && proceures.length > 0)
      return (
         <ul className="space-y-2">
            {proceures.map((proceure) => (
               <li key={proceure.id}>
                  <AppointmentCard appointment={proceure} />
               </li>
            ))}
         </ul>
      );
}

function ProceduresData() {
   const { data: procedures, isPending } = useQuery({
      queryKey: queryKeys.procedures,
      queryFn: getProcedures
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );
   if (!isPending && procedures?.length === 0) {
      <>No data</>;
   }
   if (procedures && procedures.length > 0)
      return (
         <ul className="space-y-2">
            {procedures.map((procedure) => (
               <li key={procedure.id}>
                  <AppointmentCard appointment={procedure} />
               </li>
            ))}
         </ul>
      );
}

export { ProceduresPage };
