import { render, screen } from '@testing-library/react'
import { Header } from '../../src/components/layout/Header'
import { NextIntlClientProvider } from 'next-intl'

// Mock the navigation functions
jest.mock('next-intl/navigation', () => ({
  Link: ({ children, href, ...props }: any) => <a href={href} {...props}>{children}</a>,
  usePathname: () => '/nl',
}))

const messages = {
  navigation: {
    home: 'Home',
    search: 'Search',
    favorites: 'Favorites',
    compare: 'Compare'
  }
}

describe('Header', () => {
  it('renders navigation items', () => {
    render(
      <NextIntlClientProvider locale="nl" messages={messages}>
        <Header />
      </NextIntlClientProvider>
    )

    expect(screen.getByText('Home')).toBeInTheDocument()
    expect(screen.getByText('Search')).toBeInTheDocument()
    expect(screen.getByText('Favorites')).toBeInTheDocument()
    expect(screen.getByText('Compare')).toBeInTheDocument()
  })

  it('renders Buurtinzicht brand', () => {
    render(
      <NextIntlClientProvider locale="nl" messages={messages}>
        <Header />
      </NextIntlClientProvider>
    )

    expect(screen.getByText('Buurtinzicht')).toBeInTheDocument()
  })
})