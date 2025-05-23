import { StrictMode, useEffect } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { Router } from "./router/Router";

import {
   QueryClient,
   QueryClientProvider,
   useQuery
} from "@tanstack/react-query";
import { getCurrentUser } from "./api/queries";
import { queryKeys } from "./config/queryKeys";
import { userAtom, userFetchingStateAtom } from "./store/atoms";
import { useSetAtom } from "jotai";

function App() {
   const setUser = useSetAtom(userAtom);
   const setUserFetchingState = useSetAtom(userFetchingStateAtom);

   const {
      data: userFetchedData = null,
      isPending,
      isError,
      isSuccess
   } = useQuery({
      queryKey: queryKeys.currentUser,
      queryFn: getCurrentUser
   });

   useEffect(() => {
      if (isError) setUserFetchingState("error");
      if (isPending) setUserFetchingState("fetching");
      if (isSuccess) {
         setUser(userFetchedData);
         console.log(userFetchedData);
         setUserFetchingState(userFetchedData ? "completed" : "error");
      }
   }, [isSuccess, isPending, isError]);

   return <Router />;
}

const queryClinet = new QueryClient({
   defaultOptions: {
      queries: {
         retry: false
      }
   }
});

createRoot(document.getElementById("root")!).render(
   <StrictMode>
      <QueryClientProvider client={queryClinet}>
         <App />
      </QueryClientProvider>
   </StrictMode>
);
