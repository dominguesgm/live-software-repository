class CreateMethodInvocations < ActiveRecord::Migration[5.1]
  def change
    create_table :method_invocations do |t|
      t.string :invocation
      t.references :i_method, foreign_key: true

      t.timestamps
    end
  end
end
