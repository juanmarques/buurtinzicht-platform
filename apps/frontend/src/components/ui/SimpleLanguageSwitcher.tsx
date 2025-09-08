'use client';

import { useState } from 'react';
import { ChevronDown } from 'lucide-react';

const languages = [
  { code: 'nl', name: 'Nederlands', flag: '🇳🇱' },
  { code: 'fr', name: 'Français', flag: '🇫🇷' },
  { code: 'en', name: 'English', flag: '🇬🇧' }
];

export function SimpleLanguageSwitcher() {
  const [currentLanguage, setCurrentLanguage] = useState('nl');
  const [isOpen, setIsOpen] = useState(false);

  const handleLanguageChange = (langCode: string) => {
    setCurrentLanguage(langCode);
    setIsOpen(false);
    // In a real implementation, this would change the language
    // For now, it's just a visual component
    console.log('Language changed to:', langCode);
  };

  const current = languages.find(lang => lang.code === currentLanguage) || languages[0];

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center space-x-2 px-3 py-2 rounded-lg border border-neutral-200 bg-white hover:bg-neutral-50 transition-colors"
        aria-label="Change language"
      >
        <span className="text-sm">{current.flag}</span>
        <span className="text-sm font-medium text-neutral-700 uppercase">{current.code}</span>
        <ChevronDown className="h-4 w-4 text-neutral-500" />
      </button>

      {isOpen && (
        <>
          <div 
            className="fixed inset-0 z-10" 
            onClick={() => setIsOpen(false)}
            aria-hidden="true"
          />
          <div className="absolute right-0 mt-2 py-2 w-48 bg-white rounded-lg shadow-lg border border-neutral-200 z-20">
            {languages.map((language) => (
              <button
                key={language.code}
                onClick={() => handleLanguageChange(language.code)}
                className={`w-full text-left px-4 py-2 text-sm hover:bg-neutral-50 flex items-center space-x-3 transition-colors ${
                  language.code === currentLanguage 
                    ? 'bg-primary-50 text-primary-600 font-medium' 
                    : 'text-neutral-700'
                }`}
              >
                <span className="text-base">{language.flag}</span>
                <span>{language.name}</span>
                {language.code === currentLanguage && (
                  <span className="ml-auto text-primary-600">✓</span>
                )}
              </button>
            ))}
          </div>
        </>
      )}
    </div>
  );
}