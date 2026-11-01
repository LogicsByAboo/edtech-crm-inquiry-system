import React, { useState } from 'react';
import { inquiryService } from '../services/inquiryService';
import { Inquiry, InquirySubmission } from '../types';
import { RequestException } from '../services/api';
import { Send, CheckCircle2, AlertCircle, Sparkles } from 'lucide-react';

export const PublicInquiryPage: React.FC = () => {
  const [formData, setFormData] = useState<InquirySubmission>({
    name: '',
    email: '',
    phone: '',
    userType: 'Parent',
    category: 'Admission',
    message: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [createdInquiry, setCreatedInquiry] = useState<Inquiry | null>(null);
  const [generalError, setGeneralError] = useState<string | null>(null);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Full name is required';
    } else if (formData.name.trim().length < 2 || formData.name.trim().length > 100) {
      newErrors.name = 'Name must be between 2 and 100 characters';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email address is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email.trim())) {
      newErrors.email = 'Please provide a valid email address';
    }

    if (!formData.phone.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!/^[0-9+()\-\s]{7,30}$/.test(formData.phone.trim())) {
      newErrors.phone = 'Phone number must be between 7 and 30 characters (digits, +, -, spaces)';
    }

    if (!formData.message.trim()) {
      newErrors.message = 'Message is required';
    } else if (formData.message.trim().length < 5 || formData.message.trim().length > 5000) {
      newErrors.message = 'Message must be between 5 and 5000 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => {
        const copy = { ...prev };
        delete copy[name];
        return copy;
      });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setGeneralError(null);

    if (!validate()) {
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await inquiryService.submitInquiry(formData);
      setCreatedInquiry(response);
    } catch (err: unknown) {
      if (err instanceof RequestException) {
        if (err.details) {
          setErrors(err.details);
        }
        setGeneralError(err.message);
      } else {
        setGeneralError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReset = () => {
    setFormData({
      name: '',
      email: '',
      phone: '',
      userType: 'Parent',
      category: 'Admission',
      message: '',
    });
    setErrors({});
    setCreatedInquiry(null);
    setGeneralError(null);
  };

  return (
    <div style={{ maxWidth: '720px', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <div
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.35rem 0.85rem',
            background: 'var(--primary-light)',
            color: 'var(--primary)',
            borderRadius: '9999px',
            fontSize: '0.85rem',
            fontWeight: 600,
            marginBottom: '0.75rem',
          }}
        >
          <Sparkles size={16} />
          Student & Academic Admissions
        </div>
        <h1 style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--slate-900)', marginBottom: '0.5rem' }}>
          Connect With Our Academic Counselors
        </h1>
        <p style={{ color: 'var(--slate-600)', fontSize: '1rem' }}>
          Have inquiries regarding admissions, scholarships, concerts, or general curricula? Submit your query below and our team will get in touch promptly.
        </p>
      </div>

      {createdInquiry ? (
        <div className="card" style={{ textAlign: 'center', padding: '2.5rem' }}>
          <div
            style={{
              width: '64px',
              height: '64px',
              background: 'var(--success-bg)',
              color: 'var(--success-text)',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 1.25rem',
            }}
          >
            <CheckCircle2 size={36} />
          </div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--slate-900)', marginBottom: '0.5rem' }}>
            Inquiry Successfully Submitted!
          </h2>
          <p style={{ color: 'var(--slate-600)', marginBottom: '1.5rem' }}>
            Thank you, <strong>{createdInquiry.name}</strong>. Your inquiry has been persisted in our CRM under Reference ID <strong>#{createdInquiry.id}</strong>. A confirmation email has been logged to <strong>{createdInquiry.email}</strong>.
          </p>

          <div
            style={{
              background: 'var(--slate-50)',
              border: '1px solid var(--slate-200)',
              borderRadius: 'var(--radius)',
              padding: '1rem',
              textAlign: 'left',
              marginBottom: '1.75rem',
              fontSize: '0.875rem',
            }}
          >
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
              <div><strong>User Type:</strong> {createdInquiry.userType}</div>
              <div><strong>Category:</strong> {createdInquiry.category}</div>
              <div><strong>Phone:</strong> {createdInquiry.phone}</div>
              <div><strong>Status:</strong> {createdInquiry.status}</div>
            </div>
          </div>

          <button onClick={handleReset} className="btn btn-primary">
            Submit Another Inquiry
          </button>
        </div>
      ) : (
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Inquiry Form</h2>
            <p className="card-subtitle">All fields are required. Please provide accurate contact information.</p>
          </div>

          {generalError && (
            <div className="alert alert-danger">
              <AlertCircle size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
              <div>{generalError}</div>
            </div>
          )}

          <form onSubmit={handleSubmit} noValidate>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="userType">
                  I am a <span style={{ color: '#ef4444' }}>*</span>
                </label>
                <select
                  id="userType"
                  name="userType"
                  className={`form-select ${errors.userType ? 'is-invalid' : ''}`}
                  value={formData.userType}
                  onChange={handleChange}
                  disabled={isSubmitting}
                >
                  <option value="Parent">Parent</option>
                  <option value="Student">Student</option>
                  <option value="School">School</option>
                </select>
                {errors.userType && <div className="form-error">{errors.userType}</div>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="category">
                  Inquiry Category <span style={{ color: '#ef4444' }}>*</span>
                </label>
                <select
                  id="category"
                  name="category"
                  className={`form-select ${errors.category ? 'is-invalid' : ''}`}
                  value={formData.category}
                  onChange={handleChange}
                  disabled={isSubmitting}
                >
                  <option value="Scholarship">Scholarship</option>
                  <option value="Concert">Concert</option>
                  <option value="Admission">Admission</option>
                  <option value="General Query">General Query</option>
                </select>
                {errors.category && <div className="form-error">{errors.category}</div>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="name">
                Full Name <span style={{ color: '#ef4444' }}>*</span>
              </label>
              <input
                id="name"
                type="text"
                name="name"
                placeholder="e.g. Eleanor Vance"
                className={`form-input ${errors.name ? 'is-invalid' : ''}`}
                value={formData.name}
                onChange={handleChange}
                disabled={isSubmitting}
              />
              {errors.name && <div className="form-error">{errors.name}</div>}
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="email">
                  Email Address <span style={{ color: '#ef4444' }}>*</span>
                </label>
                <input
                  id="email"
                  type="email"
                  name="email"
                  placeholder="name@example.com"
                  className={`form-input ${errors.email ? 'is-invalid' : ''}`}
                  value={formData.email}
                  onChange={handleChange}
                  disabled={isSubmitting}
                />
                {errors.email && <div className="form-error">{errors.email}</div>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="phone">
                  Phone Number <span style={{ color: '#ef4444' }}>*</span>
                </label>
                <input
                  id="phone"
                  type="tel"
                  name="phone"
                  placeholder="+1 (555) 000-0000"
                  className={`form-input ${errors.phone ? 'is-invalid' : ''}`}
                  value={formData.phone}
                  onChange={handleChange}
                  disabled={isSubmitting}
                />
                {errors.phone && <div className="form-error">{errors.phone}</div>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="message">
                Message / Details <span style={{ color: '#ef4444' }}>*</span>
              </label>
              <textarea
                id="message"
                name="message"
                rows={4}
                placeholder="Please describe your specific inquiry or question in detail..."
                className={`form-textarea ${errors.message ? 'is-invalid' : ''}`}
                value={formData.message}
                onChange={handleChange}
                disabled={isSubmitting}
              />
              {errors.message && <div className="form-error">{errors.message}</div>}
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', padding: '0.75rem' }}
              disabled={isSubmitting}
            >
              <Send size={16} />
              {isSubmitting ? 'Submitting Inquiry...' : 'Submit Inquiry'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
};
