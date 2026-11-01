import React from 'react';
import { Link } from 'react-router-dom';
import { HelpCircle, ArrowLeft } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div style={{ textAlign: 'center', padding: '4rem 1rem' }}>
      <div
        style={{
          width: '64px',
          height: '64px',
          background: 'var(--slate-100)',
          color: 'var(--slate-500)',
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          margin: '0 auto 1.25rem',
        }}
      >
        <HelpCircle size={36} />
      </div>
      <h1 style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--slate-900)', marginBottom: '0.5rem' }}>
        404 — Page Not Found
      </h1>
      <p style={{ color: 'var(--slate-600)', marginBottom: '1.5rem', maxWidth: '400px', margin: '0 auto 1.5rem' }}>
        The requested page could not be located. It may have been moved or does not exist.
      </p>
      <Link to="/" className="btn btn-primary">
        <ArrowLeft size={16} />
        Back to Home
      </Link>
    </div>
  );
};
