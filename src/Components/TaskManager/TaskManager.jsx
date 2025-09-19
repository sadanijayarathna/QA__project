import React, { useState, useEffect } from 'react';
import './TaskManager_new.css';

const TaskManager = () => {
  const [tasks, setTasks] = useState([]);
  const [newTask, setNewTask] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [editId, setEditId] = useState(null);
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editPriority, setEditPriority] = useState('MEDIUM');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [serverStatus, setServerStatus] = useState('checking'); // checking, online, offline

  // Check server connection
  const checkServerConnection = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/test', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      });
      setServerStatus('online');
      return true;
    } catch (error) {
      setServerStatus('offline');
      setError('Backend server is not running. Please start the server on port 8080.');
      return false;
    }
  };

  // Get auth token from localStorage
  const getAuthToken = () => {
    return localStorage.getItem('token');
  };

  // API call helper with authentication
  const apiCall = async (url, options = {}) => {
    const token = getAuthToken();
    if (!token) {
      setError('Please login first');
      return null;
    }

    const defaultOptions = {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    };

    const mergedOptions = {
      ...defaultOptions,
      ...options,
      headers: { ...defaultOptions.headers, ...options.headers }
    };

    try {
      // Add timeout to prevent hanging requests
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 15000); // 15 second timeout

      const response = await fetch(url, {
        ...mergedOptions,
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      
      if (response.status === 401) {
        setError('Session expired. Please login again.');
        localStorage.removeItem('token');
        return null;
      }

      if (!response.ok) {
        throw new Error(`Server error (${response.status})`);
      }

      // Check if response has content
      const contentLength = response.headers.get('content-length');
      const contentType = response.headers.get('content-type');
      
      if (contentLength === '0' || !contentType?.includes('application/json')) {
        return {}; // Return empty object for successful operations with no content
      }

      return await response.json();
    } catch (error) {
      console.error('API call failed:', error);
      
      if (error.name === 'AbortError') {
        setError('Request timed out. Please check if the server is running.');
      } else if (error.message.includes('Failed to fetch')) {
        setError('Cannot connect to server. Please ensure the backend server is running on port 8080.');
      } else {
        setError(`Connection error: ${error.message}`);
      }
      return null;
    }
  };

  // Load tasks when component mounts
  useEffect(() => {
    const initializeApp = async () => {
      const isServerOnline = await checkServerConnection();
      if (isServerOnline) {
        loadTasks();
      }
    };
    
    initializeApp();
  }, []);

  const loadTasks = async () => {
    setLoading(true);
    setError('');
    
    const data = await apiCall('http://localhost:8080/api/tasks');
    if (data) {
      setTasks(data);
    }
    setLoading(false);
  };

  const addTask = async () => {
    if (!newTask.trim()) return;
    
    setLoading(true);
    setError('');

    const taskData = {
      title: newTask,
      description: newDescription || '',
      status: 'PENDING',
      priority: 'MEDIUM'
    };

    const data = await apiCall('http://localhost:8080/api/tasks', {
      method: 'POST',
      body: JSON.stringify(taskData)
    });

    if (data) {
      setTasks([...tasks, data]);
      setNewTask('');
      setNewDescription('');
    }
    setLoading(false);
  };

  const deleteTask = async (id) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    setLoading(true);
    setError('');

    const response = await apiCall(`http://localhost:8080/api/tasks/${id}`, {
      method: 'DELETE'
    });

    if (response !== null) {
      setTasks(tasks.filter(task => task.id !== id));
    }
    setLoading(false);
  };

  const startEdit = (task) => {
    setEditId(task.id);
    setEditTitle(task.title);
    setEditDescription(task.description || '');
    setEditPriority(task.priority || 'MEDIUM');
  };

  const saveEdit = async (id) => {
    if (!editTitle.trim()) return;
    
    setLoading(true);
    setError('');

    const taskData = {
      title: editTitle,
      description: editDescription,
      priority: editPriority,
      status: tasks.find(t => t.id === id)?.status || 'PENDING'
    };

    const data = await apiCall(`http://localhost:8080/api/tasks/${id}`, {
      method: 'PUT',
      body: JSON.stringify(taskData)
    });

    if (data) {
      setTasks(tasks.map(task => task.id === id ? data : task));
      setEditId(null);
      setEditTitle('');
      setEditDescription('');
      setEditPriority('MEDIUM');
    }
    setLoading(false);
  };

  const toggleComplete = async (id) => {
    const task = tasks.find(t => t.id === id);
    if (!task) return;

    setLoading(true);
    setError('');

    const newStatus = task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED';
    const taskData = {
      title: task.title,
      description: task.description || '',
      priority: task.priority || 'MEDIUM',
      status: newStatus
    };

    const data = await apiCall(`http://localhost:8080/api/tasks/${id}`, {
      method: 'PUT',
      body: JSON.stringify(taskData)
    });

    if (data) {
      setTasks(tasks.map(t => t.id === id ? data : t));
    }
    setLoading(false);
  };

  return (
    <div className="task-bg">
      <div className="task-container">
        <h2>Task Tracker</h2>
        
        {/* Server Status Indicator */}

        
        {error && (
          <div className="error-message">
            ⚠️ {error}
            <button onClick={() => setError('')}>×</button>
          </div>
        )}

        <div className="add-task">
          <div className="add-task-header">
            <span className="form-icon">📝</span>
            <h3 className="add-task-title">Add New Task</h3>
          </div>
          <div className="add-task-inputs">
            <div className="input-group">
              <input
                type="text"
                placeholder="Enter task title..."
                value={newTask}
                onChange={e => setNewTask(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && !e.shiftKey && addTask()}
              />
            </div>
            <div className="input-group">
              <textarea
                placeholder="Enter task description "
                value={newDescription}
                onChange={e => setNewDescription(e.target.value)}
              />
            </div>
          </div>
          <div className="add-task-actions">
            <button 
              onClick={addTask} 
              disabled={loading || !newTask.trim()}
            >
              {loading ? 'Adding...' : 'Add Task'}
            </button>
          </div>
        </div>

        {loading && tasks.length === 0 && (
          <div className="loading-state">
            <div className="loading-spinner"></div>
            <p>Loading tasks...</p>
          </div>
        )}

        <ul className="task-list">
          {tasks.map(task => (
            <li key={task.id} className={task.status === 'COMPLETED' ? 'completed' : ''}>
              {editId === task.id ? (
                <div className="edit-form">
                  <input
                    type="text"
                    value={editTitle}
                    onChange={e => setEditTitle(e.target.value)}
                    placeholder="Task title"
                  />
                  <textarea
                    value={editDescription}
                    onChange={e => setEditDescription(e.target.value)}
                    placeholder="Task description"
                  />
                  <select 
                    value={editPriority} 
                    onChange={e => setEditPriority(e.target.value)}
                  >
                    <option value="LOW">Low Priority</option>
                    <option value="MEDIUM">Medium Priority</option>
                    <option value="HIGH">High Priority</option>
                  </select>
                  <div className="edit-form-actions">
                    <button 
                      onClick={() => saveEdit(task.id)}
                      disabled={loading}
                    >
                      <span className="form-icon">💾</span>
                      {loading ? 'Saving...' : 'Save'}
                    </button>
                    <button 
                      className="cancel"
                      onClick={() => setEditId(null)} 
                      disabled={loading}
                    >
                      <span className="form-icon">❌</span>Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div className="task-content" onClick={() => toggleComplete(task.id)}>
                    <div className="task-header">
                      <span className="form-icon">
                        {task.status === 'COMPLETED' ? '✅' : '⬜'}
                      </span>
                      <span className="task-title">
                        {task.title}
                      </span>
                      <span className={`task-priority ${task.priority}`}>
                        {task.priority}
                      </span>
                    </div>
                    {task.description && (
                      <div className="task-description">
                        {task.description}
                      </div>
                    )}
                    <div className="task-meta">
                      Status: {task.status} • Created: {new Date(task.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                  <div className="task-actions">
                    <button 
                      onClick={() => startEdit(task)}
                      disabled={loading}
                    >
                      <span className="form-icon">✏️</span>Edit
                    </button>
                    <button 
                      onClick={() => deleteTask(task.id)}
                      disabled={loading}
                    >
                      <span className="form-icon">🗑️</span>Delete
                    </button>
                  </div>
                </>
              )}
            </li>
          ))}
        </ul>

        {!loading && tasks.length === 0 && (
          <div className="empty-state">
            <span className="empty-icon">📝</span>
            <p>No tasks yet. Create your first task above!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default TaskManager;
