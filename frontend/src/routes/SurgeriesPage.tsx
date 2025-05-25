import { useAtomValue } from "jotai";
import { userAtom } from "../store/atoms";
import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { getCurrentUser, getSurgeries } from "../api/queries";
import { AppointmentCard } from "../components/AppointmentCard";

function SurgeriesPage() {
   const user = useAtomValue(userAtom);

   if (!user) return <>User is not Defined</>;

   return (
      <>
         <h1 className="text-center text-3xl font-bold">
            {user?.role === "PATIENT" ? "Your Surgeries" : "Surgeries"}
         </h1>
         <section className="max-w-[650px] mx-auto mt-4">
            <SurgeriesProxy role={user?.role} />
         </section>
      </>
   );
}

function SurgeriesProxy({ role }: { role: string }) {
   if (role === "PATIENT") return <SurgeriesPatientData />;
   return <SurgeriesData />;
}

function SurgeriesPatientData() {
   const { data: surgeries, isPending } = useQuery({
      queryKey: queryKeys.curerntUserSurgeries,
      queryFn: getCurrentUser.surgeries
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );
      
   if (surgeries && surgeries?.length === 0) {
      <>No data</>;
   }
   if (surgeries && surgeries.length > 0)
      return (
         <ul className="space-y-2">
            {surgeries.map((surgery) => (
               <li key={surgery.id}>
                  <AppointmentCard appointment={surgery} />
               </li>
            ))}
         </ul>
      );
}

function SurgeriesData() {
   const { data: surgeries, isPending } = useQuery({
      queryKey: queryKeys.surgeries,
      queryFn: getSurgeries
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );

   if (surgeries && surgeries.length === 0) {
      <>No data</>;
   }
   if (surgeries && surgeries.length > 0)
      return (
         <ul className="space-y-2">
            {surgeries.map((surgery) => (
               <li key={surgery.id}>
                  <AppointmentCard appointment={surgery} />
               </li>
            ))}
         </ul>
      );
}

export { SurgeriesPage };
