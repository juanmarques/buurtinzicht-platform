'use client';

import React from 'react';
// import { UserProvider } from '@auth0/nextjs-auth0/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      retry: (failureCount, error: unknown) => {
        // Don't retry on 4xx errors except 429 (rate limit)
        const err = error as any;
        if (err?.response?.status >= 400 && err?.response?.status < 500) {
          if (err?.response?.status === 429) return failureCount < 3;
          return false;
        }
        // Retry on 5xx and network errors
        return failureCount < 3;
      },
    },
    mutations: {
      retry: (failureCount, error: unknown) => {
        // Don't retry mutations on 4xx errors
        const err = error as any;
        if (err?.response?.status >= 400 && err?.response?.status < 500) {
          return false;
        }
        // Retry on 5xx and network errors
        return failureCount < 2;
      },
    },
  },
});

interface ProvidersProps {
  children: React.ReactNode;
}

export function Providers({ children }: ProvidersProps) {
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}