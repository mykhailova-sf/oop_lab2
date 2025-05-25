import { NavLink, Outlet } from "react-router";
import { routes } from "../config/routes.config";
import { useAtom, useAtomValue, useSetAtom } from "jotai";
import { userAtom, userFetchingStateAtom } from "../store/atoms";
import { useEffect, type PropsWithChildren } from "react";
import { RoleBasedIcon } from "./RoleBasedIcon";
import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../config/queryKeys";
import { getCurrentUser, getProcedures, getSurgeries } from "../api/queries";
import { ConsultationIcon, ProcedureIcon, SurgeryIcon } from "../assets/icons";
import type { AppointmentResponse } from "../types/appointmentTypes";
import type { ConsultationResponse } from "../types/consultationTypes";

function Layout() {
   const [user, setUser] = useAtom(userAtom);
   const setUserFetchingState = useSetAtom(userFetchingStateAtom);

   const {
      data: userFetchedData = null,
      isPending,
      isError,
   } = useQuery({
      queryKey: queryKeys.currentUser,
      queryFn: getCurrentUser
   });

   useEffect(() => {
      if (isError) {
         setUserFetchingState("error");
         setUser(null);
         return;
      }
      if (isPending) {
         setUserFetchingState("fetching");
         setUser(null);
         return;
      }
      if (userFetchedData) {
         setUser(userFetchedData);
         setUserFetchingState(userFetchedData ? "completed" : "error");
      }
   }, [userFetchedData, isPending, isError]);

   return (
      <>
         <div className="flex">
            <aside className="h-screen border-r border-base-300 bg-base-200 w-[200px] p-4 shadow flex flex-col">
               <NavLink to={routes.home}>
                  <button className="flex btn btn-ghost w-full text-xl">
                     <span>Drum</span>
                     <span className="block text-secondary text-shadow-xs rounded">
                        Clinic
                     </span>
                  </button>
               </NavLink>
               <div className="grow">
                  <RoleBaseNavigation />
               </div>
               <div>
                  {user && (
                     <NavLink to={routes.profile}>
                        <button className="btn btn-ghost w-full text-base flex items-center">
                           <span>Profile</span>
                           <RoleBasedIcon className="w-8" />
                        </button>
                     </NavLink>
                  )}
                  {!user && (
                     <NavLink to={routes.login}>
                        <button className="btn btn-secondary w-full">
                           Login / Sign up
                        </button>
                     </NavLink>
                  )}
               </div>
            </aside>
            <main className="container mx-auto p-4">
               <Outlet />
            </main>
         </div>
      </>
   );
}

function RoleBaseNavigation() {
   const user = useAtomValue(userAtom);
   const userFetchingState = useAtomValue(userFetchingStateAtom);
   const isFetching = userFetchingState === "fetching";
   if (isFetching) {
      return (
         <ul className="space-y-2">
            <li className="skeleton h-8" />
            <li className="skeleton h-8" />
         </ul>
      );
   }

   if (!user)
      return <div className="text-center mt-2">No pages availdable</div>;

   if (user.role === "NURSE") return <NurseNavigateion />;

   if (user.role === "PATIENT") return <PantientNavigation />;

   if (user.role === "DOCTOR") return <DoctorNavigation />;

   return <>Nada2</>;
}

const filterPendingAppointments = (appointments: AppointmentResponse[]) => {
   const filtered = appointments.filter((app) => app.status === "pending");
   return filtered;
};

const filterPendingConsultations = (consultations: ConsultationResponse[]) => {
   const filtered = consultations.filter((con) => con.status === "pending");
   return filtered;
};

function NurseNavigateion() {
   const { data: procedures, isPending } = useQuery({
      queryKey: queryKeys.procedures,
      queryFn: getProcedures
   });

   const filtered = procedures ? filterPendingAppointments(procedures) : [];

   if (isPending)
      return (
         <ul>
            <li className="skeleton h-8" />
         </ul>
      );
   if (procedures)
      return (
         <ul>
            <li>
               <RoleBaseNavLink
                  to={routes.procedures}
                  number={filtered.length}
                  icon={<ProcedureIcon />}
                  title="Procedures"
               />
            </li>
         </ul>
      );
}

function PantientNavigation() {
   const { data: consultations, isPending: isFetchingConsultations } = useQuery(
      {
         queryKey: queryKeys.currentUserConsultations,
         queryFn: getCurrentUser.consultations
      }
   );

   const { data: surgeries, isPending: isFetchingSurgeries } = useQuery({
      queryKey: queryKeys.curerntUserSurgeries,
      queryFn: getCurrentUser.surgeries
   });

   const { data: procedures, isPending: isFetchingProcedures } = useQuery({
      queryKey: queryKeys.currentUserProcedures,
      queryFn: getCurrentUser.procedures
   });

   const isPending =
      isFetchingConsultations || isFetchingSurgeries || isFetchingProcedures;

   const filteredConsultations = consultations
      ? filterPendingConsultations(consultations)
      : [];
   const filteredSurgeries = surgeries
      ? filterPendingAppointments(surgeries)
      : [];
   const filteredProcedures = procedures
      ? filterPendingAppointments(procedures)
      : [];

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-8" />
            <li className="skeleton h-8" />
            <li className="skeleton h-8" />
         </ul>
      );

   return (
      <ul className="space-y-2">
         <li>
            <LayoutNavLink to={routes.doctors}>Doctors</LayoutNavLink>
         </li>
         <li>
            <RoleBaseNavLink
               to={routes.consultations}
               number={filteredConsultations?.length}
               icon={<ConsultationIcon />}
               title="Consultations"
            />
         </li>
         <li>
            <RoleBaseNavLink
               to={routes.surgeries}
               number={filteredSurgeries?.length}
               icon={<SurgeryIcon />}
               title="Surgeries"
            />
         </li>
         <li>
            <RoleBaseNavLink
               to={routes.procedures}
               number={filteredProcedures?.length}
               icon={<ProcedureIcon />}
               title="Procedures"
            />
         </li>
      </ul>
   );
}

function DoctorNavigation() {
   const { data: consultations, isPending: isFetchingConsultations } = useQuery(
      {
         queryKey: queryKeys.currentUserConsultations,
         queryFn: getCurrentUser.consultations
      }
   );

   const { data: surgeries, isPending: isFetchingSurgeries } = useQuery({
      queryKey: queryKeys.surgeries,
      queryFn: getSurgeries
   });

   const { data: procedures, isPending: isFetchingProcedures } = useQuery({
      queryKey: queryKeys.procedures,
      queryFn: getProcedures
   });

   const isPending =
      isFetchingConsultations || isFetchingSurgeries || isFetchingProcedures;

   const filteredConsultations = consultations
      ? filterPendingConsultations(consultations)
      : [];
   const filteredSurgeries = surgeries
      ? filterPendingAppointments(surgeries)
      : [];
   const filteredProcedures = procedures
      ? filterPendingAppointments(procedures)
      : [];

   if (isPending)
      return (
         <ul className="space-y-2">
            <li className="skeleton h-8" />
            <li className="skeleton h-8" />
            <li className="skeleton h-8" />
         </ul>
      );

   return (
      <ul className="space-y-2">
         <li>
            <RoleBaseNavLink
               to={routes.consultations}
               number={filteredConsultations?.length}
               icon={<ConsultationIcon />}
               title="Consultations"
            />
         </li>
         <li>
            <RoleBaseNavLink
               to={routes.surgeries}
               number={filteredSurgeries?.length}
               icon={<SurgeryIcon />}
               title="Surgeries"
            />
         </li>
         <li>
            <RoleBaseNavLink
               to={routes.procedures}
               number={filteredProcedures?.length}
               icon={<ProcedureIcon />}
               title="Procedures"
            />
         </li>
      </ul>
   );
}

type RoleBaseNavLinkProps = {
   icon: PropsWithChildren["children"];
   title: string;
   to: string;
   number?: number;
};

function RoleBaseNavLink({ icon, title, to, number }: RoleBaseNavLinkProps) {
   return (
      <LayoutNavLink to={to}>
         <span>{title}</span>
         <span className="badge badge-outline">
            <div className="w-4 shrink-0">{icon}</div>
            {number}
         </span>
      </LayoutNavLink>
   );
}

type LayoutNavLink = {
   to: string;
};

function LayoutNavLink({ to, children }: LayoutNavLink & PropsWithChildren) {
   return (
      <NavLink to={to}>
         <button className="btn btn-ghost w-full flex gap-2">{children}</button>
      </NavLink>
   );
}

export { Layout };
