import { render, screen, fireEvent } from '@testing-library/react'
import { Button } from '../../src/components/ui/Button'

describe('Button', () => {
  it('renders with default props', () => {
    render(<Button>Click me</Button>)
    
    const button = screen.getByRole('button', { name: /click me/i })
    expect(button).toBeInTheDocument()
    expect(button).toHaveClass('bg-primary-600')
  })

  it('applies variant classes correctly', () => {
    render(<Button variant="outline">Outline Button</Button>)
    
    const button = screen.getByRole('button', { name: /outline button/i })
    expect(button).toHaveClass('border-neutral-300')
  })

  it('applies size classes correctly', () => {
    render(<Button size="sm">Small Button</Button>)
    
    const button = screen.getByRole('button', { name: /small button/i })
    expect(button).toHaveClass('px-3', 'py-1.5', 'text-sm')
  })

  it('handles click events', () => {
    const handleClick = jest.fn()
    render(<Button onClick={handleClick}>Clickable</Button>)
    
    const button = screen.getByRole('button', { name: /clickable/i })
    fireEvent.click(button)
    
    expect(handleClick).toHaveBeenCalledTimes(1)
  })

  it('can be disabled', () => {
    render(<Button disabled>Disabled Button</Button>)
    
    const button = screen.getByRole('button', { name: /disabled button/i })
    expect(button).toBeDisabled()
    expect(button).toHaveClass('opacity-50')
  })
})