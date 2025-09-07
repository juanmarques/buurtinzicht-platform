'use client';

import React, { useState } from 'react';
import { 
  Mail, 
  Phone, 
  MapPin, 
  Send,
  MessageSquare,
  HelpCircle,
  Building,
  Clock
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    company: '',
    subject: '',
    message: '',
    contactReason: 'general'
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const contactReasons = [
    { value: 'general', label: 'Algemene vraag' },
    { value: 'support', label: 'Technische ondersteuning' },
    { value: 'business', label: 'Zakelijke samenwerking' },
    { value: 'data', label: 'Data of API vragen' },
    { value: 'feedback', label: 'Feedback of suggesties' }
  ];

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    // Simulate form submission
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    setIsSubmitting(false);
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <div className="min-h-screen bg-neutral-50 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 max-w-md w-full mx-4 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Send className="h-8 w-8 text-green-600" />
          </div>
          <h2 className="text-2xl font-bold text-neutral-900 mb-2">Bericht verzonden!</h2>
          <p className="text-neutral-600 mb-6">
            Bedankt voor je bericht. We nemen binnen 24 uur contact met je op.
          </p>
          <Button 
            variant="primary" 
            onClick={() => {
              setSubmitted(false);
              setFormData({
                name: '',
                email: '',
                company: '',
                subject: '',
                message: '',
                contactReason: 'general'
              });
            }}
          >
            Nieuw bericht versturen
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl font-bold text-neutral-900 mb-4">
              Contact
            </h1>
            <p className="text-xl text-neutral-600 max-w-3xl mx-auto">
              Heb je vragen, feedback of wil je samenwerken? We horen graag van je!
            </p>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16">
          {/* Contact Information */}
          <div>
            <h2 className="text-2xl font-bold text-neutral-900 mb-8">Neem contact op</h2>
            
            <div className="space-y-8">
              {/* Office */}
              <div className="flex items-start">
                <div className="bg-primary-50 rounded-lg p-3 mr-4">
                  <Building className="h-6 w-6 text-primary-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-neutral-900 mb-1">Hoofdkantoor</h3>
                  <p className="text-neutral-600">
                    Buurtinzicht BV<br />
                    Rue de la Loi 200<br />
                    1000 Brussel, België
                  </p>
                </div>
              </div>

              {/* Email */}
              <div className="flex items-start">
                <div className="bg-primary-50 rounded-lg p-3 mr-4">
                  <Mail className="h-6 w-6 text-primary-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-neutral-900 mb-1">Email</h3>
                  <p className="text-neutral-600">
                    <a href="mailto:info@buurtinzicht.be" className="text-primary-600 hover:text-primary-700">
                      info@buurtinzicht.be
                    </a>
                  </p>
                  <p className="text-sm text-neutral-500 mt-1">
                    Voor algemene vragen en ondersteuning
                  </p>
                </div>
              </div>

              {/* Phone */}
              <div className="flex items-start">
                <div className="bg-primary-50 rounded-lg p-3 mr-4">
                  <Phone className="h-6 w-6 text-primary-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-neutral-900 mb-1">Telefoon</h3>
                  <p className="text-neutral-600">
                    <a href="tel:+3225551234" className="text-primary-600 hover:text-primary-700">
                      +32 2 555 1234
                    </a>
                  </p>
                  <p className="text-sm text-neutral-500 mt-1">
                    Ma-Vr: 9:00 - 17:00 CET
                  </p>
                </div>
              </div>

              {/* Business Hours */}
              <div className="flex items-start">
                <div className="bg-primary-50 rounded-lg p-3 mr-4">
                  <Clock className="h-6 w-6 text-primary-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-neutral-900 mb-1">Openingstijden</h3>
                  <div className="text-neutral-600 space-y-1">
                    <p>Maandag - Vrijdag: 9:00 - 17:00</p>
                    <p>Weekend: Gesloten</p>
                    <p className="text-sm text-neutral-500 mt-1">
                      Voor spoedeisende technische problemen zijn we 24/7 bereikbaar
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Contact Options */}
            <div className="bg-neutral-100 rounded-lg p-6 mt-8">
              <h3 className="font-semibold text-neutral-900 mb-4">Veelgestelde vragen</h3>
              <div className="space-y-3">
                <div className="flex items-center">
                  <HelpCircle className="h-4 w-4 text-neutral-500 mr-2" />
                  <span className="text-sm text-neutral-600">Hoe nauwkeurig is jullie buurtdata?</span>
                </div>
                <div className="flex items-center">
                  <HelpCircle className="h-4 w-4 text-neutral-500 mr-2" />
                  <span className="text-sm text-neutral-600">Kan ik mijn eigen voorkeuren aanpassen?</span>
                </div>
                <div className="flex items-center">
                  <HelpCircle className="h-4 w-4 text-neutral-500 mr-2" />
                  <span className="text-sm text-neutral-600">Welke betalingsmethoden accepteren jullie?</span>
                </div>
              </div>
              <p className="text-sm text-neutral-600 mt-4">
                Bekijk onze <a href="/faq" className="text-primary-600 hover:text-primary-700">FAQ pagina</a> voor meer antwoorden.
              </p>
            </div>
          </div>

          {/* Contact Form */}
          <div>
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8">
              <div className="flex items-center mb-6">
                <MessageSquare className="h-6 w-6 text-primary-600 mr-2" />
                <h2 className="text-xl font-semibold text-neutral-900">Stuur ons een bericht</h2>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Contact Reason */}
                <div>
                  <label className="block text-sm font-medium text-neutral-700 mb-2">
                    Waarover wil je contact opnemen?
                  </label>
                  <select
                    value={formData.contactReason}
                    onChange={(e) => handleInputChange('contactReason', e.target.value)}
                    className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    required
                  >
                    {contactReasons.map(reason => (
                      <option key={reason.value} value={reason.value}>
                        {reason.label}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Name and Email */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <Input
                    label="Naam *"
                    value={formData.name}
                    onChange={(e) => handleInputChange('name', e.target.value)}
                    required
                  />
                  <Input
                    label="E-mailadres *"
                    type="email"
                    value={formData.email}
                    onChange={(e) => handleInputChange('email', e.target.value)}
                    required
                  />
                </div>

                {/* Company and Subject */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <Input
                    label="Bedrijf"
                    value={formData.company}
                    onChange={(e) => handleInputChange('company', e.target.value)}
                    helperText="Optioneel"
                  />
                  <Input
                    label="Onderwerp *"
                    value={formData.subject}
                    onChange={(e) => handleInputChange('subject', e.target.value)}
                    required
                  />
                </div>

                {/* Message */}
                <div>
                  <label className="block text-sm font-medium text-neutral-700 mb-1">
                    Bericht *
                  </label>
                  <textarea
                    value={formData.message}
                    onChange={(e) => handleInputChange('message', e.target.value)}
                    rows={6}
                    className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="Vertel ons over je vraag, probleem of idee..."
                    required
                  />
                  <p className="text-xs text-neutral-500 mt-1">
                    Hoe meer details je geeft, hoe beter we je kunnen helpen
                  </p>
                </div>

                {/* Privacy Notice */}
                <div className="bg-neutral-50 rounded-lg p-4">
                  <p className="text-sm text-neutral-600">
                    Door dit formulier in te vullen, ga je akkoord met ons{' '}
                    <a href="/privacy" className="text-primary-600 hover:text-primary-700">
                      privacybeleid
                    </a>. We gebruiken je gegevens alleen om je vraag te beantwoorden.
                  </p>
                </div>

                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  disabled={isSubmitting}
                  icon={Send}
                  className="w-full"
                >
                  {isSubmitting ? 'Versturen...' : 'Bericht versturen'}
                </Button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}