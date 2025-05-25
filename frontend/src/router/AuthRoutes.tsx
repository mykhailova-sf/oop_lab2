import { useAtomValue } from "jotai";
import { Navigate, Outlet, useLocation } from "react-router";
import { userAtom, userFetchingStateAtom } from "../store/atoms";
import { routes } from "../config/routes.config";

function LoadingScreen() {
   return (
      <div className="flex justify-center">
         <span className="loading loading-dots loading-xl" />
      </div>
   );
}

function AuthRoutes() {
   const userFetchingState = useAtomValue(userFetchingStateAtom);
   const user = useAtomValue(userAtom);
   const { pathname } = useLocation();

   if (userFetchingState === "fetching") return <LoadingScreen />;

   if (userFetchingState === "error") return <Navigate to={routes.login} />;

   if (user) {
      if (pathname !== routes.home) return <Outlet />;
      if (user.role === "PATIENT") return <Navigate to={routes.doctors} />;
      if (user.role === "DOCTOR") return <Navigate to={routes.consultations} />;
      if (user.role === "NURSE") return <Navigate to={routes.procedures} />;
   }

   return <Navigate to={routes.login} />;
}
export { AuthRoutes };
