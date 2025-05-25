import { StrictMode} from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { Router } from "./router/Router";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

const queryClinet = new QueryClient();

function App() {
   return <Router />;
}

createRoot(document.getElementById("root")!).render(
   <StrictMode>
      <QueryClientProvider client={queryClinet}>
         <App />
         <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
   </StrictMode>
);
