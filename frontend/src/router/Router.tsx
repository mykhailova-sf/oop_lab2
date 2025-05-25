import { createBrowserRouter, RouterProvider } from "react-router";
import { routes } from "../config/routes.config";
import { ProfilePage } from "../routes/authPages/ProfilePage";
import { Layout } from "../components/Layout";
import { DoctorsPage } from "../routes/DoctorsPage";
import { AuthRoutes } from "./AuthRoutes";
import { LoginPage } from "../routes/authPages/LoginPage";
import { RegistrationPage } from "../routes/authPages/RegistrationPage";
import { ProceduresPage } from "../routes/ProceduresPage";
import { ConsultationsPage } from "../routes/CosultationsPage";
import { SurgeriesPage } from "../routes/SurgeriesPage";

const router = createBrowserRouter([
   {
      element: <Layout />,
      children: [
         {
            path: routes.login,
            element: <LoginPage />
         },
         {
            path: routes.reg,
            element: <RegistrationPage />
         },
         {
            element: <AuthRoutes />,
            children: [
               {
                  path: routes.home,
                  element: <>homepage</>
               },
               {
                  path: routes.profile,
                  element: <ProfilePage />
               },

               {
                  path: routes.doctors,
                  element: <DoctorsPage />
               },
               {
                  path: routes.consultations,
                  element: <ConsultationsPage />
               },
               {
                  path: routes.procedures,
                  element: <ProceduresPage />
               },
               {
                  path: routes.surgeries,
                  element: <SurgeriesPage />
               }
            ]
         }
      ]
   }
]);

function Router() {
   return (
      <>
         <RouterProvider router={router} />
      </>
   );
}

export { Router };
