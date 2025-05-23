import { NavLink, Outlet } from "react-router";
import { routes } from "../config/routes.config";
import { UserIcon } from "@heroicons/react/24/solid";
import { useAtomValue } from "jotai";
import { userAtom, userFetchingStateAtom } from "../store/atoms";
import type { UserResponse } from "../types/userTypes";
import type { PropsWithChildren } from "react";

function Layout() {
   const user = useAtomValue(userAtom);
   const userFetchingState = useAtomValue(userFetchingStateAtom);

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
                  <NavLink to={routes.profile}>
                     <button className="btn btn-ghost w-full text-base flex">
                        <span>Profile</span>
                        <span>
                           <UserIcon className="size-6" />
                        </span>
                     </button>
                  </NavLink>
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

   if (!user) return <>No pages available</>;

   if (user.role === "NURSE")
      return (
         <ul>
            <li>
               <LayoutNavLink to={routes.procedures}>Procedures</LayoutNavLink>
            </li>
         </ul>
      );

   if (user.role === "PATIENT")
      return (
         <ul>
            <li>
               <LayoutNavLink to={routes.doctors}>Doctors</LayoutNavLink>
            </li>
            <li>
               <LayoutNavLink to={routes.consultations}>
                  Consultations
               </LayoutNavLink>
            </li>
            <li>
               <LayoutNavLink to={routes.procedures}>Procedures</LayoutNavLink>
            </li>
            <li>
               <LayoutNavLink to={routes.surgeries}>Surgeries</LayoutNavLink>
            </li>
         </ul>
      );

   if (user.role === "DOCTOR")
      return (
         <ul>
            <li>
               <LayoutNavLink to={routes.consultations}>
                  Consultations
               </LayoutNavLink>
            </li>
            <li>
               <LayoutNavLink to={routes.procedures}>Procedures</LayoutNavLink>
            </li>
            <li>
               <LayoutNavLink to={routes.surgeries}>Surgeries</LayoutNavLink>
            </li>
         </ul>
      );

   return <>Nada2</>;
}

type LayoutNavLink = {
   to: string;
};

function LayoutNavLink({ to, children }: LayoutNavLink & PropsWithChildren) {
   return (
      <NavLink to={to}>
         <button className="btn btn-ghost w-full">{children}</button>
      </NavLink>
   );
}

export { Layout };
