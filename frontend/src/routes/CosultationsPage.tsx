import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { getCurrentUser } from "../api/queries";
import { ConsultationCard } from "../components/ConsultationCard";

function ConsultationsPage() {
   return (
      <>
         <h1 className="text-center text-3xl font-bold">Your consultations</h1>
         <section className="max-w-[650px] mx-auto mt-4">
            <ConsultationsData />
         </section>
      </>
   );
}

function ConsultationsData() {
   const { data: consultations, isPending } = useQuery({
      queryKey: queryKeys.currentUserConsultations,
      queryFn: getCurrentUser.consultations
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );
   if (!isPending && consultations?.length === 0) return <>No data</>;

   if (consultations && consultations?.length > 0)
      return (
         <ul className="space-y-2">
            {consultations.map((consultation) => (
               <li key={consultation.id}>
                  <ConsultationCard {...{ consultation }} />
               </li>
            ))}
         </ul>
      );

   return <></>;
}

export { ConsultationsPage };
