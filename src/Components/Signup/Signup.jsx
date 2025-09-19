import React, { useState } from 'react';
import './Signup_new.css';

const Signup = ({ onSignup, onSwitchToLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password || !confirmPassword) {
      setError('Please fill in all fields.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }
    
    setError('');
    setLoading(true);
    
    try {
      const response = await fetch('http://localhost:8080/api/auth/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: email, // Using email as username
          email: email,
          password: password
        }),
      });
      
      const data = await response.json();
      
      if (response.ok) {
        // Registration successful, switch to login
        alert('Registration successful! Please login with your credentials.');
        onSwitchToLogin();
      } else {
        setError(data.message || 'Registration failed. Please try again.');
      }
    } catch (error) {
      setError('Network error. Please check if the server is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-split-bg">
      <div className="signup-split-container">
        <div className="signup-left">
          <div className="company-logo">🚀 TASK TRACKER</div>
          <h1>Join us today...</h1>
          <p>Create your account to start tracking tasks effectively.<br/>Monitor progress and achieve your goals faster.</p>
          <div className="signup-left-footer">✨ Your tracking journey starts here</div>
        </div>
        <div className="signup-right">
          <form className="signup-form" onSubmit={handleSubmit}>
            <h2>Sign Up</h2>
            <p className="signup-desc">Create your account to get started with task tracking and progress monitoring.</p>
            <div className="input-group">
              <input
                type="email"
                placeholder="  Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="input-group">
              <input
                type="password"
                placeholder="  Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <div className="input-group">
              <input
                type="password"
                placeholder="  Confirm Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>
            {error && <div className="error">⚠️ {error}</div>}
            <button type="submit" className="signup-btn" disabled={loading}>
              {loading ? 'SIGNING UP...' : 'SIGN UP'}
            </button>
            <div className="signup-links">
               <button className='login-link' type="button" onClick={onSwitchToLogin}>Login</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Signup;
