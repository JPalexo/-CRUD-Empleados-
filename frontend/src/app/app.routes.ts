import { Routes } from '@angular/router';
import { adminGuard } from './core/admin.guard';
import { AdminAccessPage } from './pages/admin-access/admin-access.page';
import { DashboardPage } from './pages/dashboard/dashboard.page';
import { DepartamentosPage } from './pages/departamentos/departamentos.page';
import { EmployeeLoginPage } from './pages/employee-login/employee-login.page';
import { EmpleadosPage } from './pages/empleados/empleados.page';

export const routes: Routes = [
	{
		path: '',
		component: DashboardPage
	},
	{
		path: 'admin-access',
		component: AdminAccessPage
	},
	{
		path: 'empleados',
		component: EmpleadosPage,
		canActivate: [adminGuard]
	},
	{
		path: 'departamentos',
		component: DepartamentosPage,
		canActivate: [adminGuard]
	},
	{
		path: 'employee-login',
		component: EmployeeLoginPage
	},
	{
		path: '**',
		redirectTo: ''
	}
];
