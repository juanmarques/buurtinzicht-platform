import * as React from 'react';
import Link from 'next/link';
import { cn } from '../../lib/utils';
import { LucideIcon } from 'lucide-react';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
  icon?: LucideIcon;
  iconPosition?: 'left' | 'right';
  href?: string;
}

const buttonVariants = {
  primary: 'bg-primary-600 hover:bg-primary-700 focus:ring-primary-500 text-white border-transparent',
  secondary: 'bg-secondary-600 hover:bg-secondary-700 focus:ring-secondary-500 text-white border-transparent',
  outline: 'bg-transparent hover:bg-neutral-50 focus:ring-primary-500 text-neutral-700 border-neutral-300',
  ghost: 'bg-transparent hover:bg-neutral-100 focus:ring-primary-500 text-neutral-700 border-transparent',
  danger: 'bg-error-600 hover:bg-error-700 focus:ring-error-500 text-white border-transparent',
};

const buttonSizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm',
  lg: 'px-6 py-3 text-base',
};

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ 
    className, 
    variant = 'primary', 
    size = 'md', 
    loading = false,
    icon: Icon,
    iconPosition = 'left',
    children,
    disabled,
    href,
    ...props 
  }, ref) => {
    const isDisabled = disabled || loading;
    
    const baseClassName = cn(
      'inline-flex items-center justify-center rounded-lg border font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2',
      'disabled:opacity-50 disabled:cursor-not-allowed',
      buttonVariants[variant],
      buttonSizes[size],
      className
    );

    const content = (
      <>
        {loading && (
          <svg
            className={cn('animate-spin h-4 w-4', children && 'mr-2')}
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
        )}
        
        {Icon && !loading && iconPosition === 'left' && (
          <Icon className={cn('h-4 w-4', children && 'mr-2')} />
        )}
        
        {children}
        
        {Icon && !loading && iconPosition === 'right' && (
          <Icon className={cn('h-4 w-4', children && 'ml-2')} />
        )}
      </>
    );

    if (href) {
      return (
        <Link href={href} className={baseClassName}>
          {content}
        </Link>
      );
    }

    return (
      <button
        ref={ref}
        className={baseClassName}
        disabled={isDisabled}
        {...props}
      >
        {content}
      </button>
    );
  }
);

Button.displayName = 'Button';

export { Button };