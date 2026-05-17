import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeModule, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

interface AccountNode {
  code: string;
  name: string;
  type: string;
  balance: number;
  children?: AccountNode[];
}

interface FlatAccountNode {
  code: string;
  name: string;
  type: string;
  balance: number;
  level: number;
  expandable: boolean;
}

const ACCOUNT_TREE: AccountNode[] = [
  {
    code: '1', name: 'Assets', type: 'HEADER', balance: 0,
    children: [
      {
        code: '1.1', name: 'Current Assets', type: 'HEADER', balance: 0,
        children: [
          { code: '1.1.1', name: 'Cash & Bank', type: 'ASSET', balance: 5000000000 },
          { code: '1.1.2', name: 'Accounts Receivable', type: 'ASSET', balance: 2500000000 },
          { code: '1.1.3', name: 'Inventory', type: 'ASSET', balance: 1800000000 },
        ],
      },
      {
        code: '1.2', name: 'Fixed Assets', type: 'HEADER', balance: 0,
        children: [
          { code: '1.2.1', name: 'Building', type: 'ASSET', balance: 15000000000 },
          { code: '1.2.2', name: 'Equipment', type: 'ASSET', balance: 3000000000 },
        ],
      },
    ],
  },
  {
    code: '2', name: 'Liabilities', type: 'HEADER', balance: 0,
    children: [
      {
        code: '2.1', name: 'Current Liabilities', type: 'HEADER', balance: 0,
        children: [
          { code: '2.1.1', name: 'Accounts Payable', type: 'LIABILITY', balance: 1200000000 },
          { code: '2.1.2', name: 'Short-term Loans', type: 'LIABILITY', balance: 500000000 },
        ],
      },
    ],
  },
  {
    code: '3', name: 'Equity', type: 'HEADER', balance: 0,
    children: [
      { code: '3.1', name: 'Share Capital', type: 'EQUITY', balance: 10000000000 },
      { code: '3.2', name: 'Retained Earnings', type: 'EQUITY', balance: 7500000000 },
    ],
  },
  {
    code: '4', name: 'Revenue', type: 'HEADER', balance: 0,
    children: [
      { code: '4.1', name: 'Product Sales', type: 'REVENUE', balance: 8000000000 },
      { code: '4.2', name: 'Service Revenue', type: 'REVENUE', balance: 3500000000 },
    ],
  },
  {
    code: '5', name: 'Expenses', type: 'HEADER', balance: 0,
    children: [
      {
        code: '5.1', name: 'Operating Expenses', type: 'HEADER', balance: 0,
        children: [
          { code: '5.1.1', name: 'Salaries & Wages', type: 'EXPENSE', balance: 4200000000 },
          { code: '5.1.2', name: 'Utilities', type: 'EXPENSE', balance: 350000000 },
        ],
      },
    ],
  },
];

function flattenNode(node: AccountNode, level: number): FlatAccountNode {
  return {
    code: node.code,
    name: node.name,
    type: node.type,
    balance: node.balance,
    level,
    expandable: !!node.children?.length,
  };
}

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [
    CommonModule, MatTreeModule, MatIconModule, MatButtonModule,
    MatFormFieldModule, MatSelectModule,
  ],
  styles: [`
    :host { display: block; padding: 1.5rem; }
    .badge { display: inline-block; padding: 0.0625rem 0.5rem; border-radius: 9999px; font-size: 0.6875rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.025em; }
    .badge-asset { background-color: #dbeafe; color: #1e40af; }
    .badge-liability { background-color: #fef3c7; color: #92400e; }
    .badge-equity { background-color: #d1fae5; color: #065f46; }
    .badge-revenue { background-color: #e0e7ff; color: #3730a3; }
    .badge-expense { background-color: #fee2e2; color: #991b1b; }
    .badge-header { background-color: #f3f4f6; color: #6b7280; }
    .tree-node { display: flex; align-items: center; gap: 0.5rem; min-height: 2.5rem; padding: 0.25rem 0.5rem; cursor: pointer; }
    .tree-node:hover { background: #f9fafb; border-radius: 0.375rem; }
    .tree-node .code { font-family: monospace; font-size: 0.8125rem; color: #6b7280; min-width: 80px; }
    .tree-node .name { font-weight: 500; color: #111827; flex: 1; }
    .tree-node .balance { font-family: monospace; font-size: 0.875rem; font-weight: 500; color: #374151; min-width: 140px; text-align: right; }
  `],
  template: `
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 m-0">Chart of Accounts</h1>
      <button mat-flat-button color="primary">
        <mat-icon>add</mat-icon> Add Account
      </button>
    </div>

    <mat-form-field appearance="outline" class="min-w-[200px] mb-4">
      <mat-label>Filter by type</mat-label>
      <mat-select (selectionChange)="typeFilter.set($any($event).value)">
        <mat-option value="">All Types</mat-option>
        <mat-option value="ASSET">Asset</mat-option>
        <mat-option value="LIABILITY">Liability</mat-option>
        <mat-option value="EQUITY">Equity</mat-option>
        <mat-option value="REVENUE">Revenue</mat-option>
        <mat-option value="EXPENSE">Expense</mat-option>
      </mat-select>
    </mat-form-field>

    <mat-tree [dataSource]="dataSource" [treeControl]="treeControl">
      <mat-tree-node *matTreeNodeDef="let node" matTreeNodeToggle class="tree-node"
                     [style.padding-left.px]="node.level * 24 + 8">
        @if (node.expandable) {
          <button mat-icon-button matTreeNodeToggle>
            <mat-icon>{{ treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right' }}</mat-icon>
          </button>
        } @else {
          <button mat-icon-button disabled class="invisible">
            <mat-icon>chevron_right</mat-icon>
          </button>
        }
        <span class="code" [class.font-bold]="node.expandable">{{ node.code }}</span>
        <span class="name" [class.font-bold]="node.expandable">{{ node.name }}</span>
        <span class="balance" [class.font-bold]="node.expandable">{{ node.balance | number:'1.2-2' }}</span>
        <span class="badge"
          [class.badge-asset]="node.type === 'ASSET'"
          [class.badge-liability]="node.type === 'LIABILITY'"
          [class.badge-equity]="node.type === 'EQUITY'"
          [class.badge-revenue]="node.type === 'REVENUE'"
          [class.badge-expense]="node.type === 'EXPENSE'"
          [class.badge-header]="node.type === 'HEADER'">{{ node.type }}</span>
      </mat-tree-node>
    </mat-tree>
  `
})
export class AccountListComponent {
  protected readonly typeFilter = signal('');

  private readonly transformer = (node: AccountNode, level: number): FlatAccountNode =>
    flattenNode(node, level);

  protected readonly treeControl = new FlatTreeControl<FlatAccountNode>(
    node => node.level,
    node => node.expandable,
  );

  private readonly treeFlattener = new MatTreeFlattener(
    this.transformer,
    node => node.level,
    node => node.expandable,
    node => node.children,
  );

  protected readonly dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  constructor() {
    this.dataSource.data = ACCOUNT_TREE;
  }
}
