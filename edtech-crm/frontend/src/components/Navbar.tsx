import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { GraduationCap, LogIn, LogOut, ShieldCheck, FileText } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { admin, isAuthenticated, logout } = useAuth();
  const location = useLocation();

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand">
          <div className="brand-icon">
            <GraduationCap size={20} />
          </div>
          <span>EdTech Portal</span>
        </Link>

        <nav className="nav-links">
          <Link
            to="/"
            className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
          >
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
              <FileText size={16} />
              Submit Inquiry
            </span>
          </Link>

          {isAuthenticated ? (
            <>
              <Link
                to="/admin"
                className={`nav-link ${location.pathname === '/admin' ? 'active' : ''}`}
              >
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  <ShieldCheck size={16} />
                  Dashboard
                </span>
              </Link>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginLeft: '0.5rem' }}>
                <span style={{ fontSize: '0.85rem', color: 'var(--slate-500)' }}>
                  {admin?.name}
                </span>
                <button
                  onClick={logout}
                  className="btn btn-secondary"
                  style={{ padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
                  title="Sign out of Admin Dashboard"
                >
                  <LogOut size={14} />
                  Logout
                </button>
              </div>
            </>
          ) : (
            <Link
              to="/login"
              className={`nav-link ${location.pathname === '/login' ? 'active' : ''}`}
            >
              <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                <LogIn size={16} />
                Admin Login
              </span>
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
};
