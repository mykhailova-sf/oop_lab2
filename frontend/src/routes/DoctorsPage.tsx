import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { getUsers } from "../api/queries";
import { DoctorCard } from "../components/DoctorCard";

function DoctorsPage() {
   return (
      <>
         <h1 className="text-center text-3xl font-bold">Available doctors</h1>
         <section className="max-w-[650px] mx-auto mt-4">
            <DoctorsData />
         </section>
      </>
   );
}

function DoctorsData() {
   const { data: doctros, isPending } = useQuery({
      queryKey: queryKeys.doctors,
      queryFn: getUsers.doctors
   });

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-[82px]" />
            <li className="skeleton h-[82px]" />
         </ul>
      );
   if (!isPending && doctros?.length === 0) {
      <>No data</>;
   }
   if (doctros && doctros.length > 0)
      return (
         <ul className="space-y-2">
            {doctros.map((doctor) => (
               <li key={doctor.id}>
                  <DoctorCard {...{ doctor }} />
               </li>
            ))}
         </ul>
      );
}

export { DoctorsPage };
