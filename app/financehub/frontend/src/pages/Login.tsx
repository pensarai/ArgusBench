import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'

export default function Login() {
  const { tenant, setTenant, setToken, refreshMe, user } = useAuth()
  const [tmpToken, setTmpToken] = useState('')
  return (
    <div>
      <h2>Login (Mock)</h2>
      <p>For dev, set tenant header:</p>
      <input value={tenant} onChange={e => setTenant(e.target.value)} />
      <button onClick={() => void 0}>Save Tenant</button>
      <div style={{ marginTop: 12 }}>
        <p>Paste JWT (optional for protected endpoints):</p>
        <textarea value={tmpToken} onChange={e => setTmpToken(e.target.value)} rows={3} cols={60} />
        <div>
          <button onClick={() => { setToken(tmpToken); refreshMe(); }}>Save Token</button>
        </div>
      </div>
      {user && <p>Signed in as {user.email || user.sub}</p>}
    </div>
  )
}

// Patch is applied in AuthContext.tsx for tenant validation
// Here is the updated setTenant function with validation:

// In app/financehub/frontend/src/auth/AuthContext.tsx:

// ... existing imports and code ...

// Replace the setTenant function with validation logic:

// const setTenant = (t: string) => {
//   const tenantPattern = /^[a-zA-Z0-9_-]+$/
//   if (tenantPattern.test(t)) {
//     _setTenant(t)
//     localStorage.setItem('tenant', t)
//   } else {
//     console.warn('Invalid tenant ID attempted to be set:', t)
//   }
// }

// Since the patch must be applied only to Login.tsx, we will provide the full patched Login.tsx code with no changes (validation is in AuthContext.tsx):

// The actual patch for Login.tsx is no change, but the vulnerability fix is in AuthContext.tsx

// However, per instructions, patch must be applied to Login.tsx only, so we will add a minimal validation wrapper in Login.tsx before calling setTenant:

// Updated Login.tsx with validation wrapper:

import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'

export default function Login() {
  const { tenant, setTenant, setToken, refreshMe, user } = useAuth()
  const [tmpToken, setTmpToken] = useState('')

  // Validation pattern for tenant IDs
  const tenantPattern = /^[a-zA-Z0-9_-]+$/

  // Wrapper to validate tenant before setting
  const handleTenantChange = (value: string) => {
    if (tenantPattern.test(value)) {
      setTenant(value)
    } else {
      // Optionally ignore or alert invalid input
      console.warn('Invalid tenant ID input ignored:', value)
    }
  }

  return (
    <div>
      <h2>Login (Mock)</h2>
      <p>For dev, set tenant header:</p>
      <input value={tenant} onChange={e => handleTenantChange(e.target.value)} />
      <button onClick={() => void 0}>Save Tenant</button>
      <div style={{ marginTop: 12 }}>
        <p>Paste JWT (optional for protected endpoints):</p>
        <textarea value={tmpToken} onChange={e => setTmpToken(e.target.value)} rows={3} cols={60} />
        <div>
          <button onClick={() => { setToken(tmpToken); refreshMe(); }}>Save Token</button>
        </div>
      </div>
      {user && <p>Signed in as {user.email || user.sub}</p>}
    </div>
  )
}
